package com.safeqr.app.qrcode.service;

import com.safeqr.app.qrcode.dto.QRCodePayload;
import com.safeqr.app.qrcode.dto.URLVerificationResponse;
import org.springframework.stereotype.Service;

@Service
public class URLVerificationService {

    public URLVerificationResponse verifyURL(QRCodePayload payload) {
        URLVerificationResponse response = new URLVerificationResponse();
        try {
            java.net.URL url = new java.net.URL(payload.getData());
            String protocol = url.getProtocol();
            if ("https".equalsIgnoreCase(protocol)) {
                response.setSecure(true);
                response.setMessage("The connection is secure.");
            } else {
                response.setSecure(false);
                response.setMessage("The connection is not secure.");
            }
        } catch (Exception e) {
            response.setSecure(false);
            response.setMessage("Invalid URL.");
        }
        return response;
    }
}