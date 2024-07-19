package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.EmailEntity;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.URLEntity;
import com.safeqr.app.qrcode.service.URLVerificationService;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;

@EqualsAndHashCode(callSuper = true)
@Data
public final class URLModel extends QRCodeModel<URLEntity> {
    private static final Logger logger = LoggerFactory.getLogger(URLModel.class);
    @JsonIgnore
    private final URLVerificationService urlVerificationService;

    @Autowired
    public URLModel(QRCodeEntity scannedQRCodeEntity, URLVerificationService urlVerificationService) {
        this.scannedQRCode = scannedQRCodeEntity;
        this.urlVerificationService = urlVerificationService;
        details = null;
    }

    @Override
    public void setDetails() {
        String url = scannedQRCode.getContents();
        try {
            details = urlVerificationService.breakdownURL(url);
            urlVerificationService.countAndTrackRedirects(url, details);
            // set qrCode Identifier
            details.setQrCodeId(scannedQRCode.getId());

            // Insert into URL table
            urlVerificationService.insertDB(details);

        } catch (IOException | URISyntaxException e) {
            logger.error("Error: ", e);
        }
    }
    @Override
    public URLEntity getDetails () {
        return urlVerificationService.getURLEntityByQRCodeId(scannedQRCode.getId());
    }
}
