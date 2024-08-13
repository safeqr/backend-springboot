package com.safeqr.app.qrcodetips.controller;

import com.safeqr.app.qrcodetips.entity.QrCodeTipEntity;
import com.safeqr.app.qrcodetips.service.QrCodeTipsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static com.safeqr.app.constants.APIConstants.*;

@RestController
@RequestMapping(API_VERSION)
public class QRCodeTipsController {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeTipsController.class);
    QrCodeTipsService qrCodeTipsService;

    @Autowired
    public QRCodeTipsController (QrCodeTipsService qrCodeTipsService) { this.qrCodeTipsService = qrCodeTipsService;}

    @GetMapping(value = API_URL_TIPS_GET)
    public ResponseEntity<QrCodeTipEntity> getRandomTips() {
        logger.info("Invoking GET QR Code tips endpoint");
        return ResponseEntity.ok(qrCodeTipsService.getTips());
    }
}
