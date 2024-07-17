package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.EmailEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.service.EmailVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmailModel extends QRCodeModel {
    private static final Logger logger = LoggerFactory.getLogger(EmailModel.class);

    @JsonIgnore
    private final EmailVerificationService emailVerificationService;

    EmailEntity details;

    public EmailModel(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity, EmailVerificationService emailVerificationService) {
        this.scannedQRCode = scannedQRCodeEntity;
        this.qrCode = qrCodeTypeEntity;
        this.emailVerificationService = emailVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        details = EmailEntity.builder().qrCodeId(scannedQRCode.getId()).build();
        // Insert into email table
        emailVerificationService.insertDB(details);
    }
}