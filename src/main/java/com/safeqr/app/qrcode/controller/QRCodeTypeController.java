package com.safeqr.app.qrcode.controller;

import com.safeqr.app.constants.APIConstants;
import com.safeqr.app.constants.CommonConstants;
import com.safeqr.app.qrcode.dto.QRCodePayload;
import com.safeqr.app.qrcode.dto.RedirectCountResponse;
import com.safeqr.app.qrcode.dto.URLVerificationResponse;
import com.safeqr.app.qrcode.dto.response.BaseScanResponse;
import com.safeqr.app.qrcode.entity.QRCodeType;
import com.safeqr.app.qrcode.service.QRCodeTypeService;
import com.safeqr.app.qrcode.service.RedirectCountService;
import com.safeqr.app.qrcode.service.URLVerificationService;
import com.safeqr.app.qrcode.service.VirusTotalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping(APIConstants.API_VERSION)
public class QRCodeTypeController {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeTypeService.class);

    @Autowired
    private QRCodeTypeService qrCodeTypeService;

    @Autowired
    private URLVerificationService urlVerificationService;

    @Autowired
    private VirusTotalService virusTotalService;

    @Autowired
    private RedirectCountService redirectCountService;

    @GetMapping(value = APIConstants.API_URL_QRCODE_GET_ALL)
    public ResponseEntity<List<QRCodeType>> getAllTypes() {
        return ResponseEntity.ok(qrCodeTypeService.getAllTypes());
    }

    @PostMapping(value = APIConstants.API_URL_QRCODE_SCAN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseScanResponse> scanQRCode(@RequestBody QRCodePayload payload,
                                                       @RequestHeader(required = false, name = CommonConstants.HEADER_USER_ID) String userId) {
        logger.info("Invoking scan endpoint");
        return ResponseEntity.ok(qrCodeTypeService.scanQRCode(userId, payload));
    }

    @PostMapping(APIConstants.API_URL_QRCODE_DETECT)
    public ResponseEntity<String> detectType(@RequestBody QRCodePayload payload) {
        return ResponseEntity.ok(qrCodeTypeService.detectType(payload).block());
    }

    @PostMapping(APIConstants.API_URL_QRCODE_VERIFY_URL)
    public ResponseEntity<URLVerificationResponse> verifyURL(@RequestBody QRCodePayload payload) {
        URLVerificationResponse response = urlVerificationService.verifyURL(payload);
        return ResponseEntity.ok(response);
    }

    @PostMapping(APIConstants.API_URL_QRCODE_VIRUS_TOTAL_CHECK)
    public ResponseEntity<Boolean> virusTotalCheck(@RequestBody QRCodePayload payload) {
        try {
            String analysisId = virusTotalService.scanURL(payload);
            boolean isSafe = virusTotalService.getAnalysis(analysisId);
            return ResponseEntity.ok(isSafe);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(false);
        }
    }

    @PostMapping(APIConstants.API_URL_QRCODE_REDIRECT_COUNT)
    public ResponseEntity<RedirectCountResponse> checkRedirects(@RequestBody QRCodePayload payload) {
        return ResponseEntity.ok(redirectCountService.countRedirects(payload).block());
    }

}