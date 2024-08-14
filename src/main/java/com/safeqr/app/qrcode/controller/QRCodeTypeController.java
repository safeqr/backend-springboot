package com.safeqr.app.qrcode.controller;

import static com.safeqr.app.constants.APIConstants.*;
import static com.safeqr.app.constants.CommonConstants.*;
import com.safeqr.app.qrcode.dto.request.QRCodePayload;
import com.safeqr.app.qrcode.dto.response.BaseScanResponse;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.service.QRCodeTypeService;
import com.safeqr.app.qrcode.service.URLVerificationService;
import com.safeqr.app.qrcode.service.VirusTotalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(API_VERSION)
public class QRCodeTypeController {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeTypeController.class);

    @Autowired
    private QRCodeTypeService qrCodeTypeService;

    @Autowired
    private URLVerificationService urlVerificationService;

    @Autowired
    private VirusTotalService virusTotalService;

    @GetMapping(value = API_URL_QRCODE_GET_ALL)
    public ResponseEntity<List<QRCodeTypeEntity>> getAllTypes() {
        return ResponseEntity.ok(qrCodeTypeService.getAllTypes());
    }
    @GetMapping(value = API_URL_QRCODE_GET_QR_DETAILS)
    public ResponseEntity<BaseScanResponse> getScannedQRCodeDetails(@RequestHeader(name="QR-ID") UUID qrCodeId) {
        logger.info("Invoking GET QRCode details endpoint, qrCodeId: {}", qrCodeId);
        return ResponseEntity.ok(qrCodeTypeService.getScannedQRCodeDetails(qrCodeId));
    }

    @PostMapping(value = API_URL_QRCODE_SCAN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseScanResponse> scanQRCode(@RequestBody QRCodePayload payload,
                                                       @RequestHeader(required = false, name = HEADER_USER_ID) String userId) {
        logger.info("User Id Invoking scan endpoint: {}", userId);
        return ResponseEntity.ok(qrCodeTypeService.scanQRCode(userId, payload));
    }

    @PostMapping(value = API_URL_QRCODE_VERIFY_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseScanResponse> verifyURL(@RequestBody QRCodePayload payload,
                                                      @RequestHeader(required = false, name = HEADER_USER_ID) String userId) {
        logger.info("User Id Invoking verify url endpoint: {}", userId);
        return ResponseEntity.ok(qrCodeTypeService.scanQRCode(userId, payload));

    }

    @PostMapping(API_URL_QRCODE_VIRUS_TOTAL_CHECK)
    public ResponseEntity<Boolean> virusTotalCheck(@RequestBody QRCodePayload payload) {
        try {
            String analysisId = virusTotalService.scanURL(payload);
            boolean isSafe = virusTotalService.getAnalysis(analysisId);
            return ResponseEntity.ok(isSafe);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(false);
        }
    }


}