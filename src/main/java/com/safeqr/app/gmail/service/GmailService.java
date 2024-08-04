package com.safeqr.app.gmail.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.qrcode.encoder.QRCode;
import com.safeqr.app.gmail.dto.ScannedGmailResponseDto;
import com.safeqr.app.gmail.entity.GmailCidEntity;
import com.safeqr.app.gmail.entity.GmailEmailEntity;
import com.safeqr.app.gmail.entity.GmailUrlEntity;
import com.safeqr.app.gmail.model.EmailMessage;
import com.safeqr.app.gmail.model.QRCodeByContentId;
import com.safeqr.app.gmail.model.QRCodeByURL;
import com.safeqr.app.gmail.repository.GmailCidRespository;
import com.safeqr.app.gmail.repository.GmailEmailRespository;
import com.safeqr.app.gmail.repository.GmailUrlsRespository;
import com.safeqr.app.qrcode.model.QRCodeModel;
import com.safeqr.app.qrcode.service.QRCodeTypeService;
import com.safeqr.app.user.service.UserService;
import com.safeqr.app.utils.DateParsingUtils;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.Thread;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants.TOKEN_SERVER_URL;
import static com.safeqr.app.constants.APIConstants.APPLICATION_NAME;

@Service
public class GmailService {
    @Value("${gmail.client.clientId}")
    private String clientId;

    @Value("${gmail.client.clientSecret}")
    private String clientSecret;

    private final GmailEmailRespository gmailEmailRespository;
    private final GmailCidRespository gmailCidRespository;
    private final GmailUrlsRespository gmailUrlsRespository;
    private final QRCodeTypeService qrCodeTypeService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(GmailService.class);
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final long MAX_RESULTS = 100L;

    public GmailService(GmailEmailRespository gmailEmailRespository,
                        GmailCidRespository gmailCidRespository,
                        GmailUrlsRespository gmailUrlsRespository,
                        QRCodeTypeService qrCodeTypeService,
                        UserService userService) {
        this.gmailEmailRespository = gmailEmailRespository;
        this.gmailCidRespository = gmailCidRespository;
        this.gmailUrlsRespository = gmailUrlsRespository;
        this.qrCodeTypeService = qrCodeTypeService;
        this.userService = userService;
    }

    private Gmail getGmailService(String accessToken, String refreshToken) throws IOException {
        Credential userCredentials = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setTokenServerUrl(new GenericUrl(TOKEN_SERVER_URL))
                .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                .build()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);

        return new Gmail.Builder(httpTransport, JSON_FACTORY, userCredentials)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    // Renew the access token if it has expired using the refresh token.
    private String refreshAccessToken(String refreshToken) throws IOException {
        TokenResponse response = new GoogleRefreshTokenRequest(
                httpTransport, JSON_FACTORY, refreshToken, clientId, clientSecret)
                .execute();
        return response.getAccessToken();
    }

    private Gmail refreshAndGetGmailService(String accessToken, String refreshToken) throws IOException {
        try {
            return getGmailService(accessToken, refreshToken);
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 401) {
                logger.info("Access token expired. Refreshing token...");
                String newAccessToken = refreshAccessToken(refreshToken);
                return getGmailService(newAccessToken, refreshToken);
            }
            throw e;
        }
    }

    // Async method to scan all emails in the user's inbox to prevent timeout.
    @Async
    public void getEmailAsync(String userId, String accessToken, String refreshToken) {
        try {
            ScannedGmailResponseDto result = getEmail(userId, accessToken, refreshToken);
            CompletableFuture.completedFuture(result);
        } catch (IOException e) {
            logger.error("Error processing Gmail", e);
        }
    }
    // Scan all emails in the user's inbox.
    public ScannedGmailResponseDto getEmail(String userId, String accessToken, String refreshToken) throws IOException {
        Gmail service = refreshAndGetGmailService(accessToken, refreshToken);
        logger.info("Gmail service initialized: {}", service);

        List<EmailMessage> emailMessagesList = new ArrayList<>();
        String meUserId = "me";
        String nextPageToken = null;
        // Fetching email messages with page token and setting max results, Default value is 100.
        do {

//            ListHistoryResponse historyResponse = service.users().history().list(meUserId)
//                    .setStartHistoryId(BigInteger.valueOf(689335))
//                    .execute();
//
//            List<History> historyList = historyResponse.getHistory();
//
//            for (History history : historyList) {
//                logger.info("History Id: {}, Message Id: {}, Message Snippet: {}", history.getId(), history.getMessages().get(0).getId(), history.getMessages().get(0).getHistoryId());
//            }
            ListMessagesResponse listResponse = fetchMessages(service, meUserId, nextPageToken);
            List<Message> messages = listResponse.getMessages();
            nextPageToken = listResponse.getNextPageToken();

            // Iterate all the messages and save to gmail db only if it has a valid QR code.
            for (Message message : messages) {
                EmailMessage emailMessage = processMessage(service, meUserId, message);
                if (emailMessage != null) {
                    emailMessagesList.add(emailMessage);
                    // Save email message to database.
                    saveEmailMessageAndScanQRCode(userId, emailMessage);
                }
            }
        } while (nextPageToken != null);

        // Update user's history id.
        // TODO: Update user's history id.

        return new ScannedGmailResponseDto(emailMessagesList);
    }
    // Save email message to database and scan QR code.
    private void saveEmailMessageAndScanQRCode(String userId, EmailMessage emailMessage) {
        GmailEmailEntity gmailEmailEntity = saveEmailMessage(userId, emailMessage);

        if (gmailEmailEntity != null) {
            // Save QR codes by content ID
            saveQRCodeByContentId(gmailEmailEntity, emailMessage.getQrCodeByContentId());

            // Save QR codes by URL
            saveQRCodeByURL(gmailEmailEntity, emailMessage.getQrCodeByURL());
        } else {
            logger.warn("Skipping QR code processing due to failure in saving email message.");
        }
    }
    // Save to gmail_email table
    private GmailEmailEntity saveEmailMessage(String userId, EmailMessage emailMessage) {
        logger.info("userId: {}", userId);
        OffsetDateTime dateReceived = DateParsingUtils.parseDate(emailMessage.getDate());
        try {
            GmailEmailEntity gmailEmailEntity = GmailEmailEntity.builder()
                    .userId(userId)
                    .messageId(emailMessage.getMessageId())
                    .threadId(emailMessage.getThreadId())
                    .historyId(Long.valueOf(emailMessage.getHistoryId()))
                    .subject(emailMessage.getSubject())
                    .dateReceived(dateReceived)
                    .build();
            return gmailEmailRespository.save(gmailEmailEntity);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                logger.warn("Duplicate entry for userId: {}, messageId: {}", userId, emailMessage.getMessageId());
            } else {
                logger.error("Error saving to gmail_email table: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("Error saving gmail_email table: {}", e.getMessage(), e);
        }
        return null;
    }

    // Iterate through decoded contents found in attachment as content id and save to gmail_cid table
    private void saveQRCodeByContentId(GmailEmailEntity gmailEmailEntity, List<QRCodeByContentId> qrCodeByContentIdList) {
        qrCodeByContentIdList.forEach(cid -> {
            cid.getDecodedContent().forEach(decodedContent -> {
                try {
                    QRCodeModel<?> qrCodeModel = qrCodeTypeService.scanGmailDecodedContents(gmailEmailEntity.getUserId(), decodedContent);
                    GmailCidEntity gmailCidEntity = GmailCidEntity.builder()
                            .gmailId(gmailEmailEntity.getId())
                            .qrCodeId(qrCodeModel.getData().getId())
                            .cid(cid.getCid())
                            .attachmentId(cid.getAttachmentId())
                            .decodedContent(decodedContent)
                            .build();
                    gmailCidRespository.save(gmailCidEntity);
                } catch (Exception e) {
                    logger.error("Error saving QR code by content ID to gmail_cid table: {}", e.getMessage(), e);
                }
            });
        });
    }
    // Iterate through decoded content found in url and save to gmail_url table
    private void saveQRCodeByURL(GmailEmailEntity gmailEmailEntity, List<QRCodeByURL> qrCodeByURLList) {
        qrCodeByURLList.forEach(imageUrl -> {
            imageUrl.getDecodedContent().forEach(decodedContent -> {
                try {
                    QRCodeModel<?> qrCodeModel = qrCodeTypeService.scanGmailDecodedContents(gmailEmailEntity.getUserId(), decodedContent);
                    GmailUrlEntity gmailUrlEntity = GmailUrlEntity.builder()
                            .gmailId(gmailEmailEntity.getId())
                            .qrCodeId(qrCodeModel.getData().getId())
                            .imageUrl(imageUrl.getUrl())
                            .decodedContent(decodedContent)
                            .build();
                    gmailUrlsRespository.save(gmailUrlEntity);
                } catch (Exception e) {
                    logger.error("Error saving QR code by URL to gmail_urls table: {}", e.getMessage(), e);
                }
            });
        });
    }

    // Fetching Scanned Gmail from database
    public ScannedGmailResponseDto fetchScannedGmail(String userId){
        // Fetching all emails from gmail_email table
        List<GmailEmailEntity> userEmailsList = gmailEmailRespository.findByUserId(userId);
        List<EmailMessage> emailMessageList = new ArrayList<>();

        if (userEmailsList != null && !userEmailsList.isEmpty()) {
            userEmailsList.forEach(email -> {
                EmailMessage emailMessage = new EmailMessage(
                        email.getMessageId(),
                        email.getThreadId(),
                        email.getSubject(),
                        email.getHistoryId().toString(),
                        email.getDateReceived().toString()
                );
                // Fetching all CIDs from gmail_cid table
                List<GmailCidEntity> cidList = gmailCidRespository.findByGmailId(email.getId());
                Map<String, QRCodeByContentId> qrCodeByContentIdMap = new HashMap<>();

                for (GmailCidEntity cid : cidList) {
                    String key = cid.getCid() + "-" + cid.getAttachmentId();
                    QRCodeByContentId qrCodeByContentId = qrCodeByContentIdMap.get(key);

                    if (qrCodeByContentId == null) {
                        qrCodeByContentId = QRCodeByContentId.builder()
                                .cid(cid.getCid())
                                .attachmentId(cid.getAttachmentId())
                                .decodedContent(new ArrayList<>())
                                .totalQRCodeFound(0)
                                .build();
                        qrCodeByContentIdMap.put(key, qrCodeByContentId);
                    }

                    // Append decoded content to the existing list
                    qrCodeByContentId.getDecodedContent().add(cid.getDecodedContent());

                    // Fetch scanned QR code from database and add to message object
                    emailMessage.addQRCodeModel(qrCodeTypeService.getScannedQRCodeDetailsInModel(cid.getQrCodeId()));

                    qrCodeByContentId.setTotalQRCodeFound(qrCodeByContentId.getTotalQRCodeFound() + 1);
                }

                emailMessage.setQrCodeByContentId(new ArrayList<>(qrCodeByContentIdMap.values()));

                // Fetching all URLs from gmail_urls table
                List<GmailUrlEntity> urlList = gmailUrlsRespository.findByGmailId(email.getId());

                Map<String, QRCodeByURL> qrCodeByURLMap = new HashMap<>();

                for (GmailUrlEntity url : urlList) {
                    String key = url.getImageUrl();
                    QRCodeByURL qrCodeByURL = qrCodeByURLMap.get(key);

                    if (qrCodeByURL == null) {
                        qrCodeByURL = QRCodeByURL.builder()
                                .url(url.getImageUrl())
                                .decodedContent(new ArrayList<>())
                                .totalQRCodeFound(0)
                                .build();
                        qrCodeByURLMap.put(key, qrCodeByURL);
                    }

                    // Append decoded content to the existing list
                    qrCodeByURL.getDecodedContent().add(url.getDecodedContent());

                    // Fetch scanned QR code from database and add to message object
                    emailMessage.addQRCodeModel(qrCodeTypeService.getScannedQRCodeDetailsInModel(url.getQrCodeId()));
                    qrCodeByURL.setTotalQRCodeFound(qrCodeByURL.getTotalQRCodeFound() + 1);
                }

                emailMessage.setQrCodeByURL(new ArrayList<>(qrCodeByURLMap.values()));

                emailMessageList.add(emailMessage);

            });
        }
        return ScannedGmailResponseDto.builder().messages(emailMessageList).build();
    }
    // Fetching email messages with page token and setting max results
    private ListMessagesResponse fetchMessages(Gmail service, String userId, String pageToken) throws IOException {
        return service.users().messages().list(userId)
                .setPageToken(pageToken)
                .setMaxResults(MAX_RESULTS)
                .execute();
    }
    // Processing email message and returning EmailMessage object if it has a valid QR code.
    private EmailMessage processMessage(Gmail service, String userId, Message message) throws IOException {
        message = service.users().messages().get(userId, message.getId()).setFormat("full").execute();
        List<MessagePart> parts = message.getPayload().getParts();
        Set<String> attachmentIds = new HashSet<>();
        Set<String> imageUrls = new HashSet<>();
        processPartsRecursively(parts, attachmentIds, imageUrls);

        if (attachmentIds.isEmpty() && imageUrls.isEmpty()) {
            return null;
        }

        String subject = getHeader(message, "Subject");
        String emailDate = getHeader(message, "Date");
        logger.info("Email Subject: {}", subject);
        logger.info("Message ID: {}", message.getId());
        logger.info("History ID: {}", message.getHistoryId());

        EmailMessage emailMessage = new EmailMessage(message.getId(), message.getThreadId(), subject, String.valueOf(message.getHistoryId()), emailDate);

        processAttachments(service, message.getId(), parts, attachmentIds, emailMessage);
        processImageUrls(imageUrls, emailMessage);

        return emailMessage.hasQRCodes() ? emailMessage : null;
    }
    // Process all the attachments.
    private void processAttachments(Gmail service, String messageId, List<MessagePart> parts, Set<String> attachmentIds, EmailMessage emailMessage) throws IOException {
        for (String attachmentId : attachmentIds) {
            Optional<String> attachment = findAttachmentIdByCid(parts, attachmentId);
            if (attachment.isPresent()) {
                List<String> qrCodeValue = processAttachment(service, messageId, attachment.get());
                if (!qrCodeValue.isEmpty()) {
                    emailMessage.addQRCodeByContentId(new QRCodeByContentId(attachmentId, attachment.get(), qrCodeValue, qrCodeValue.size()));
                }
            }
        }
    }
    // Process all the image URLs.
    private void processImageUrls(Set<String> imageUrls, EmailMessage emailMessage)  {
        for (String imageUrl : imageUrls) {
            List<String> qrCodeValue = scanQRCodeFromUrl(imageUrl);
            if (!qrCodeValue.isEmpty()) {
                emailMessage.addQRCodeByURL(new QRCodeByURL(imageUrl, qrCodeValue, qrCodeValue.size()));
            }
        }
    }
    // Find the header with the given name.
    private String getHeader(Message message, String name) {
        return message.getPayload().getHeaders().stream()
                .filter(header -> name.equalsIgnoreCase(header.getName()))
                .findFirst()
                .map(MessagePartHeader::getValue)
                .orElse("No " + name);
    }
    // Find the attachment ID in the given message part.
    private Optional<String> findAttachmentIdByCid(List<MessagePart> parts, String cid) {
        return parts.stream()
                .flatMap(part -> Stream.concat(findAttachmentIdInCurrentPart(part, cid).stream(), Optional.ofNullable(part.getParts())
                .flatMap(subParts -> findAttachmentIdByCid(subParts, cid)).stream()))
                .findFirst();
    }
    // Find the attachment ID in the message subpart.
    private Optional<String> findAttachmentIdInCurrentPart(MessagePart part, String cid) {
        return Optional.ofNullable(part.getHeaders())
                .flatMap(headers -> headers.stream()
                .filter(header -> isContentIdHeader(header, cid))
                .findFirst()
                .map(header -> part.getBody().getAttachmentId()));
    }
    // Check if the header is a Content-ID header with the given CID.
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
    private List<String> scanQRCodeFromUrl(String imageUrl) {
        try {
            BufferedImage image = downloadImageFromUrl(imageUrl);
            if (image != null) {
                return decodeQRCodes(image);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid URI scheme for URL: {} -> {}", imageUrl, e.getMessage());
        } catch(URISyntaxException e) {
            logger.error("Error while scanning QR code from URL", e);
        }
        return Collections.emptyList();
    }
    // Download the image from the given URL
    private BufferedImage downloadImageFromUrl(String imageUrl) throws URISyntaxException {
        try {
            imageUrl = imageUrl.replace(" ", "%20");
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();
            logger.info("Downloading image from URL: {}", imageUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(imageUrl))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                byte[] imageBytes = response.body();
                return ImageIO.read(new ByteArrayInputStream(imageBytes));
            } else {
                logger.warn("Failed to download image. HTTP response code: {}", response.statusCode());
            }
        } catch (URISyntaxException e) {
            logger.error("Invalid URL: {} -> {}", imageUrl, e.getMessage());
        } catch (HttpTimeoutException e) {
            logger.error("Request timed out for URL: {} -> {}", imageUrl, e.getMessage());
        } catch (ConnectException e) {
            logger.warn("Failed to connect to URL: {} -> {}", imageUrl, e.getMessage());
        } catch (IOException e) {
            logger.warn("Error downloading image from URL: {} -> {}", imageUrl, e.getMessage());
            if (Thread.currentThread().isInterrupted()) {
                logger.warn("Thread was interrupted during IO operation for URL: {}", imageUrl);
            }
        } catch (InterruptedException e) {
            logger.warn("Thread was interrupted during HTTP request for URL: {} -> {}", imageUrl, e.getMessage());
            Thread.currentThread().interrupt();
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
            }
        } catch (NotFoundException e) {
            // No QR codes found
        } catch (Exception e) {
            logger.error("Error decoding QR codes", e);
        }
        if (!qrCodeValues.isEmpty())
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
