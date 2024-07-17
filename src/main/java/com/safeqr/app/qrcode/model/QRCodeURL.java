package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.entity.QRCodeURLEntity;
import com.safeqr.app.qrcode.repository.URLRepository;
import com.safeqr.app.qrcode.service.URLVerificationService;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QRCodeURL extends QRCodeModel {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeURL.class);
    @JsonIgnore
    private final URLVerificationService urlVerificationService;
    @JsonIgnore
    private final URLRepository urlRepository;

    QRCodeURLEntity details;

    public QRCodeURL(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity, URLVerificationService urlVerificationService, URLRepository urlRepository) {
        this.scannedQRCode = scannedQRCodeEntity;
        this.qrCode = qrCodeTypeEntity;
        this.urlVerificationService = urlVerificationService;
        this.urlRepository = urlRepository;
        this.details = null;
    }

    @Override
    public void insertDB() {
        String url = scannedQRCode.getContents();
        try {
            details = urlVerificationService.breakdownURL(url);
            List<String> redirectChain = urlVerificationService.countAndTrackRedirects(url);
            // set qrCode Identifier
            details.setQrCodeId(scannedQRCode.getId());
            details.setRedirect(redirectChain.size() - 1);
            details.setRedirectChain(redirectChain);

            // Insert into URL table
            urlRepository.save(details);

        } catch (IOException | URISyntaxException e) {
            logger.error("Error: ", e);
        }
    }
}
