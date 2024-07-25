package com.safeqr.app.gmail.controller;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.safeqr.app.gmail.service.GmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.imageio.ImageIO;

import static com.safeqr.app.constants.APIConstants.API_VERSION;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping(API_VERSION)
public class GmailController {
    private static final Logger logger = LoggerFactory.getLogger(GmailController.class);
    GmailService gmailService;

    private static final String APPLICATION_NAME = "SafeQR App";
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static com.google.api.services.gmail.Gmail client;

    GoogleClientSecrets clientSecrets;
    GoogleAuthorizationCodeFlow flow;
    Credential credential;

    @Value("${gmail.client.clientId}")
    private String clientId;

    @Value("${gmail.client.clientSecret}")
    private String clientSecret;

    @Value("${gmail.client.redirectUri}")
    private String redirectUri;

    @Autowired
    public GmailController(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @RequestMapping(value = "/gmail/login", method = RequestMethod.GET)
    public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
        return new RedirectView(authorize());
    }
    private String authorize() throws Exception {
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(GmailScopes.GMAIL_READONLY))
                    .build();
        }
        authorizationUrl = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setAccessType("offline")
                //.setApprovalPrompt("force") // force refresh token
                ;

        System.out.println("gmail authorizationUrl ->" + authorizationUrl);
        return authorizationUrl.build();
    }

    @RequestMapping(value = "/gmail/callback", method = RequestMethod.GET, params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {

        // System.out.println("code->" + code + " userId->" + userId + "
        // query->" + query);

        JSONObject json = new JSONObject();
        JSONArray emailArray = new JSONArray();

        // String message;
        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            credential = flow.createAndStoreCredential(response, "userID");
            logger.info(credential.getAccessToken());
            logger.info(credential.getRefreshToken());
            logger.info(credential.toString());

            // Build the Gmail service
            Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Get the list of messages
            ListMessagesResponse listResponse = service.users().messages().list("me").execute();
            List<Message> messages = listResponse.getMessages();

            if (messages != null && !messages.isEmpty()) {
                for (Message message : messages) {
                    Message fullMessage = service.users().messages().get("me", message.getId()).setFormat("full").execute();

                    if (containsQRCode(fullMessage)) {
                        JSONObject emailJson = new JSONObject();
                        emailJson.put("id", fullMessage.getId());

                        // Extract subject
                        String subject = "";
                        for (MessagePartHeader header : fullMessage.getPayload().getHeaders()) {
                            if (header.getName().equals("Subject")) {
                                subject = header.getValue();
                                break;
                            }
                        }
                        emailJson.put("subject", subject);

                        // Extract snippet
                        emailJson.put("snippet", fullMessage.getSnippet());

                        emailArray.put(emailJson);
                    }
                }
            }

            json.put("emails_with_qr_codes", emailArray);



        } catch (Exception e) {

            System.out.println("exception cached ");
            e.printStackTrace();
        }

        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    }
    private boolean containsQRCode(Message message) throws IOException {
        if (message.getPayload().getParts() != null) {
            for (MessagePart part : message.getPayload().getParts()) {
                if ("text/html".equals(part.getMimeType())) {
                    String data = new String(Base64.decodeBase64(part.getBody().getData()));
                    if (scanForQRCode(data)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean scanForQRCode(String htmlContent) {
        // Extract all img tags
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher matcher = pattern.matcher(htmlContent);

        while (matcher.find()) {
            String src = matcher.group(1);
            if (src.startsWith("data:image")) {
                // It's a base64 encoded image
                String base64Image = src.split(",")[1];
                byte[] imageBytes = Base64.decodeBase64(base64Image);

                try {
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    Result result = new MultiFormatReader().decode(bitmap);
                    if (result != null) {
                        // QR Code detected
                        return true;
                    }
                } catch (Exception e) {
                    // If there's an error reading the image or it's not a QR code, continue to the next image
                    continue;
                }
            }
        }
        return false;
    }


    @GetMapping(value = "/gmail/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticate() {
        logger.info("Invoking gmail authenticate endpoint");
        return ResponseEntity.ok(Map.of("version", "SafeQR v1.0.2"));
    }
}

