package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.EmailEntity;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.SMSEntity;
import com.safeqr.app.qrcode.service.SMSVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public final class SMSModel extends QRCodeModel<SMSEntity> {
    private static final Logger logger = LoggerFactory.getLogger(SMSModel.class);

    @JsonIgnore
    private final SMSVerificationService smsVerificationService;

    public SMSModel(QRCodeEntity scannedQRCodeEntity, SMSVerificationService smsVerificationService) {
        this.data = scannedQRCodeEntity;
        this.smsVerificationService = smsVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        details = SMSEntity.builder().qrCodeId(data.getId()).build();

        smsVerificationService.parseSMSString(details, data.getContents());
        // Insert into sms table
        smsVerificationService.insertDB(details);
    }
    @Override
    public SMSEntity getDetails () {
        return smsVerificationService.getSMSEntityByQRCodeId(data.getId());
    }
    @Override
    public String retrieveClassification() {
        return "";
    }
}