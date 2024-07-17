package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.entity.URLEntity;
import com.safeqr.app.qrcode.service.URLVerificationService;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class URLModel extends QRCodeModel {
    private static final Logger logger = LoggerFactory.getLogger(URLModel.class);
    @JsonIgnore
    private final URLVerificationService urlVerificationService;

    URLEntity details;
    @Autowired
    public URLModel(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity, URLVerificationService urlVerificationService) {
        this.scannedQRCode = scannedQRCodeEntity;
        this.qrCode = qrCodeTypeEntity;
        this.urlVerificationService = urlVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        String url = scannedQRCode.getContents();
        try {
            details = urlVerificationService.breakdownURL(url);
            List<String> redirectChain = urlVerificationService.countAndTrackRedirects(url);
            // set qrCode Identifier
            details.setQrCodeId(scannedQRCode.getId());
            details.setRedirect(redirectChain.size() - 1);
            details.setRedirectChain(redirectChain);

            // Insert into URL table
            urlVerificationService.insertDB(details);

        } catch (IOException | URISyntaxException e) {
            logger.error("Error: ", e);
        }
    }
}
