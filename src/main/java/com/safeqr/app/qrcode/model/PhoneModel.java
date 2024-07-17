package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.PhoneEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.service.PhoneVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public class PhoneModel extends QRCodeModel {
    private static final Logger logger = LoggerFactory.getLogger(PhoneModel.class);

    @JsonIgnore
    private final PhoneVerificationService phoneVerificationService;

    PhoneEntity details;

    public PhoneModel(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity, PhoneVerificationService phoneVerificationService) {
        this.scannedQRCode = scannedQRCodeEntity;
        this.qrCode = qrCodeTypeEntity;
        this.phoneVerificationService = phoneVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        details = PhoneEntity.builder().qrCodeId(scannedQRCode.getId()).build();
        // Insert into phone table
        phoneVerificationService.insertDB(details);
    }
}