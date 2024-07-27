package com.safeqr.app.gmail.controller;

import com.google.api.services.gmail.model.*;
import com.safeqr.app.gmail.dto.ScannedGmailResponseDto;
import org.apache.commons.codec.binary.Base64;
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
import static com.safeqr.app.constants.APIConstants.*;
import java.io.IOException;
import java.util.*;



@RestController
@RequestMapping(API_VERSION)
public class GmailController {
    private static final Logger logger = LoggerFactory.getLogger(GmailController.class);
    GmailService gmailService;
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

    @GetMapping(value = "/gmail/login")
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
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(GmailScopes.GMAIL_READONLY))
                    .build();
        }
        authorizationUrl = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setAccessType("offline")
                //.setApprovalPrompt("force") // force refresh token
                ;

        logger.info("gmail authorizationUrl -> {}", authorizationUrl);
        return authorizationUrl.build();
    }

    @GetMapping(value = "/gmail/callback", params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {
        JSONObject json = new JSONObject();
        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            credential = flow.createAndStoreCredential(response, "userID");
            logger.info(credential.getAccessToken());
            logger.info(credential.getRefreshToken());
            logger.info(credential.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/gmail/getEmails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserEmails(@RequestHeader(name = "accessToken") String accessToken) throws IOException, InterruptedException {
        logger.info("Invoking GET Scan User Emails endpoints");
        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>("Access token is missing", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(gmailService.getEmail(accessToken), HttpStatus.OK);
    }
}

