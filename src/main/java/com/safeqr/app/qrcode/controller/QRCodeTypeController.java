package com.safeqr.app.qrcode.controller;

import com.safeqr.app.qrcode.dto.QRCodePayload;
import com.safeqr.app.qrcode.dto.RedirectCountResponse;
import com.safeqr.app.qrcode.dto.URLVerificationResponse;
import com.safeqr.app.qrcode.entity.QRCodeType;
import com.safeqr.app.qrcode.service.QRCodeTypeService;
import com.safeqr.app.qrcode.service.RedirectCountService;
import com.safeqr.app.qrcode.service.URLVerificationService;
import com.safeqr.app.qrcode.service.VirusTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qrcodetypes")
public class QRCodeTypeController {

    @Autowired
    private QRCodeTypeService qrCodeTypeService;

    @Autowired
    private URLVerificationService urlVerificationService;

    @Autowired
    private VirusTotalService virusTotalService;

    @Autowired
    private RedirectCountService redirectCountService;

    @GetMapping
    public ResponseEntity<List<QRCodeType>> getAllTypes() {
        return ResponseEntity.ok(qrCodeTypeService.getAllTypes());
    }

    @PostMapping("/detect")
    public ResponseEntity<String> detectType(@RequestBody QRCodePayload payload) {
        return ResponseEntity.ok(qrCodeTypeService.detectType(payload).block());
    }

    @PostMapping("/verifyURL")
    public ResponseEntity<URLVerificationResponse> verifyURL(@RequestBody QRCodePayload payload) {
        URLVerificationResponse response = urlVerificationService.verifyURL(payload);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/virusTotalCheck")
    public ResponseEntity<Boolean> virusTotalCheck(@RequestBody QRCodePayload payload) {
        try {
            String analysisId = virusTotalService.scanURL(payload);
            boolean isSafe = virusTotalService.getAnalysis(analysisId);
            return ResponseEntity.ok(isSafe);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(false);
        }
    }

    @PostMapping("/checkRedirects")
    public ResponseEntity<RedirectCountResponse> checkRedirects(@RequestBody QRCodePayload payload) {
        return ResponseEntity.ok(redirectCountService.countRedirects(payload).block());
    }

}