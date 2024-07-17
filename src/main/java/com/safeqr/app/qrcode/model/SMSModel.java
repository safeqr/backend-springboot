package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.SMSEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.service.SMSVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public class SMSModel extends QRCodeModel {
    private static final Logger logger = LoggerFactory.getLogger(SMSModel.class);

    @JsonIgnore
    private final SMSVerificationService smsVerificationService;

    SMSEntity details;

    public SMSModel(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity, SMSVerificationService smsVerificationService) {
        this.scannedQRCode = scannedQRCodeEntity;
        this.qrCode = qrCodeTypeEntity;
        this.smsVerificationService = smsVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        details = SMSEntity.builder().qrCodeId(scannedQRCode.getId()).build();
        // Insert into sms table
        smsVerificationService.insertDB(details);
    }
}