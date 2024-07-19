package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.EmailEntity;

import com.safeqr.app.qrcode.service.EmailVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public final class EmailModel extends QRCodeModel<EmailEntity> {
    private static final Logger logger = LoggerFactory.getLogger(EmailModel.class);

    @JsonIgnore
    private final EmailVerificationService emailVerificationService;

    public EmailModel(QRCodeEntity scannedQRCodeEntity, EmailVerificationService emailVerificationService) {
        this.data = scannedQRCodeEntity;
        this.emailVerificationService = emailVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        details = EmailEntity.builder().qrCodeId(data.getId()).build();
        // Insert into email table
        emailVerificationService.insertDB(details);
    }

    @Override
    public EmailEntity getDetails () {
        return emailVerificationService.getEmailEntityByQRCodeId(data.getId());
    }
}