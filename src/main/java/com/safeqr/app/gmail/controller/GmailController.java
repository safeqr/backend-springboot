package com.safeqr.app.gmail.controller;

import com.safeqr.app.gmail.service.GmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.safeqr.app.constants.APIConstants.API_VERSION;

@RestController
@RequestMapping(API_VERSION)
public class GmailController {
    private static final Logger logger = LoggerFactory.getLogger(GmailController.class);
    GmailService gmailService;
    @Autowired
    public GmailController(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @GetMapping(value = "/gmail/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticate() {
        logger.info("Health Check");
        return ResponseEntity.ok(Map.of("version", "SafeQR v1.0.2"));
    }
}

