package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTextEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.repository.TextRepository;
import com.safeqr.app.qrcode.service.TextVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public class QRCodeText extends QRCodeModel {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeText.class);

    @JsonIgnore
    private final TextVerificationService textVerificationService;
    @JsonIgnore
    private final TextRepository textRepository;

    QRCodeTextEntity details;

    public QRCodeText(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity, TextVerificationService textVerificationService, TextRepository textRepository) {
        this.scannedQRCode = scannedQRCodeEntity;
        this.qrCode = qrCodeTypeEntity;
        this.textVerificationService = textVerificationService;
        this.textRepository = textRepository;
        this.details = null;
    }

    @Override
    public void insertDB() {
        details = QRCodeTextEntity.builder().qrCodeId(scannedQRCode.getId()).text(scannedQRCode.getContents()).build();
        // Insert into URL table
        textRepository.save(details);
    }
}