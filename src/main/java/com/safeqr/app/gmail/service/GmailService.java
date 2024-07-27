package com.safeqr.app.gmail.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.gmail.model.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.safeqr.app.constants.APIConstants.APPLICATION_NAME;

@Service
public class GmailService {
    private static final Logger logger = LoggerFactory.getLogger(GmailService.class);
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Gmail getGmailService(String accessToken) {
        Credential userCredentials = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
        return new Gmail.Builder(httpTransport, JSON_FACTORY, userCredentials)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public JSONObject getEmail(String accessToken) throws IOException, InterruptedException {
        JSONObject json = new JSONObject();
        JSONArray emailArray = new JSONArray();

        // Build the Gmail service
        Gmail service = getGmailService(accessToken);
        logger.info("service-> {}", service);

        // Get the list of messages
        ListMessagesResponse listResponse = service.users().messages().list("me").execute();
        List<Message> messages = listResponse.getMessages();

        for (Message message : messages) {
            message = service.users().messages().get("me", message.getId()).setFormat("full").execute();
            List<MessagePart> parts = message.getPayload().getParts();
            Set<String> attachmentIds = new HashSet<>();
            Set<String> imageUrls = new HashSet<>();
            processPartsRecursively(parts, attachmentIds, imageUrls);

            // Extract and log the email subject
            String subject = getSubject(message);
            logger.info("Email Subject-> {}", subject);

            if (attachmentIds.isEmpty() && imageUrls.isEmpty())
                continue;

            String messageId = message.getId();
            logger.info("messageId-> {}", messageId);
            String historyId = String.valueOf(message.getHistoryId());
            logger.info("historyId-> {}", historyId);

            for (String attachmentId : attachmentIds) {
                Optional<String> attachment = findAttachmentIdByCid(parts, attachmentId);
                logger.info("attachment-> {}", attachment);
                if (attachment.isPresent()) {
                    List<String> qrCodeValue = processAttachment(service, messageId, attachment.get());
                    emailArray.put(qrCodeValue);
                }
            }
            for (String imageUrl : imageUrls) {
                List<String> qrCodeValue = scanQRCodeFromUrl(imageUrl);
                if (qrCodeValue != null) {
                    emailArray.put(qrCodeValue);
                }
            }
        }
        logger.info("Total Emails-> {}", messages.size());
        json.put("qr_codes", emailArray);
        return json;
    }

    private String getSubject(Message message) {
        return message.getPayload().getHeaders().stream()
                .filter(header -> "Subject".equals(header.getName()))
                .findFirst()
                .map(MessagePartHeader::getValue)
                .orElse("No Subject");
    }
    private Optional<String> findAttachmentIdByCid(List<MessagePart> parts, String cid) {
        return parts.stream()
                .flatMap(part -> Stream.concat(findAttachmentIdInCurrentPart(part, cid).stream(), Optional.ofNullable(part.getParts())
                .flatMap(subParts -> findAttachmentIdByCid(subParts, cid)).stream()))
                .findFirst();
    }

    private Optional<String> findAttachmentIdInCurrentPart(MessagePart part, String cid) {
        return Optional.ofNullable(part.getHeaders())
                .flatMap(headers -> headers.stream()
                .filter(header -> isContentIdHeader(header, cid))
                .findFirst()
                .map(header -> part.getBody().getAttachmentId()));
    }

    private boolean isContentIdHeader(MessagePartHeader header, String cid) {
        return "Content-ID".equalsIgnoreCase(header.getName()) && header.getValue().contains(cid);
    }
    // Recursive method to handle nested parts to search for CID URIs
    private void processPartsRecursively(List<MessagePart> parts, Set<String> attachmentIds, Set<String> imageURLs) {
        if (parts != null) {
            for (MessagePart part : parts) {
                if (part.getMimeType().equalsIgnoreCase("text/html")) {
                    String html = new String(Base64.decodeBase64(part.getBody().getData()));
                    attachmentIds.addAll(extractCIDsFromHtml(html));
                    imageURLs.addAll(extractImageUrlsFromHtml(html));
                } else if (part.getParts() != null) {
                    // Recursive call to handle nested parts
                    processPartsRecursively(part.getParts(), attachmentIds, imageURLs);
                }
            }
        }
    }
    private List<String> scanQRCodeFromUrl(String imageUrl) throws IOException, InterruptedException {
        try {
            BufferedImage image = downloadImageFromUrl(imageUrl);
            if (image != null) {
                return decodeQRCodes(image);
            }
        } catch(URISyntaxException e) {
            logger.error("Error while scanning QR code from URL", e);
        }
        return null;
    }
    // Download the image from the given URL
    private BufferedImage downloadImageFromUrl(String imageUrl) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        logger.info("imageUrl-> {}", imageUrl);
        // Encode the URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl.replace(" ", "%20")))
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() == 200) {
            byte[] imageBytes = response.body();
            return ImageIO.read(new ByteArrayInputStream(imageBytes));
        } else {
            logger.error("Failed to download image. HTTP response code: {}", response.statusCode());
        }
        return null;
    }
    private List<String> processAttachment(Gmail service, String messageId, String attachmentId) throws IOException {
        MessagePartBody attachPart = service.users().messages().attachments().get("me", messageId, attachmentId).execute();
        byte[] imageBytes = Base64.decodeBase64(attachPart.getData());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        // ImageIO.write(image, "png", new File("debug_image.png"));
        return decodeQRCodes(image);
    }

    private List<String> decodeQRCodes(BufferedImage image) {
        List<String> qrCodeValues = new ArrayList<>();
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        // Set up decoding hints
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, List.of(BarcodeFormat.QR_CODE));

        try {
            QRCodeMultiReader multiReader = new QRCodeMultiReader();
            Result[] results = multiReader.decodeMultiple(bitmap, hints);

            if (results != null) {
                for (Result result : results) {
                    qrCodeValues.add(result.getText());
                    logger.info("Detected QR code: {}", result.getText());
                }
            } else {
                logger.info("No QR codes found in the image");
            }
        } catch (NotFoundException e) {
            logger.info("No QR codes found in the image");
        } catch (Exception e) {
            logger.error("Error decoding QR codes", e);
        }

        logger.info("Total QR codes found: {}", qrCodeValues.size());
        return qrCodeValues;
    }

    //Extract CIDs from HTML
    private Set<String> extractCIDsFromHtml(String html) {
        Document doc = Jsoup.parse(html);
        Elements imgs = doc.select("img[src^=cid:]");

        return imgs.stream()
                .map(img -> img.attr("src"))
                .filter(src -> src.startsWith("cid:"))
                .map(src -> src.substring(4)) // Remove "cid:" prefix
                .collect(Collectors.toSet());
    }
    //Extract image URLs from HTML
    private Set<String> extractImageUrlsFromHtml(String html) {
        Document doc = Jsoup.parse(html);
        Elements imgs = doc.select("img[src]");

        return imgs.stream()
                .map(img -> img.attr("src"))
                .filter(this::isImageUrl)
                .collect(Collectors.toSet());
    }
    // Check if the URL is an image URL
    private boolean isImageUrl(String url) {
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") || lowerUrl.endsWith(".png") || lowerUrl.endsWith(".gif") || lowerUrl.endsWith(".bmp");
    }

}
