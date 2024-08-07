package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.EmailEntity;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.TextEntity;
import com.safeqr.app.qrcode.service.TextVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public final class TextModel extends QRCodeModel<TextEntity> {
    private static final Logger logger = LoggerFactory.getLogger(TextModel.class);

    @JsonIgnore
    private final TextVerificationService textVerificationService;

    public TextModel(QRCodeEntity scannedQRCodeEntity, TextVerificationService textVerificationService) {
        this.data = scannedQRCodeEntity;
        this.textVerificationService = textVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        details = TextEntity.builder().qrCodeId(data.getId()).text(data.getContents()).build();
        // Insert into text table
        textVerificationService.insertDB(details);
    }
    @Override
    public TextEntity getDetails () {
        return textVerificationService.getTextEntityByQRCodeId(data.getId());
    }

    @Override
    public String retrieveClassification() {
        return "";
    }
}