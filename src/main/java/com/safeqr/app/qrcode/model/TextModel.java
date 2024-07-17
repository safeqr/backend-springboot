package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.TextEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.service.TextVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public class TextModel extends QRCodeModel {
    private static final Logger logger = LoggerFactory.getLogger(TextModel.class);

    @JsonIgnore
    private final TextVerificationService textVerificationService;

    TextEntity details;

    public TextModel(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity, TextVerificationService textVerificationService) {
        this.scannedQRCode = scannedQRCodeEntity;
        this.qrCode = qrCodeTypeEntity;
        this.textVerificationService = textVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        details = TextEntity.builder().qrCodeId(scannedQRCode.getId()).text(scannedQRCode.getContents()).build();
        // Insert into text table
        textVerificationService.insertDB(details);
    }
}