package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.EmailEntity;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.PhoneEntity;
import com.safeqr.app.qrcode.service.PhoneVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public final class PhoneModel extends QRCodeModel<PhoneEntity> {
    private static final Logger logger = LoggerFactory.getLogger(PhoneModel.class);

    @JsonIgnore
    private final PhoneVerificationService phoneVerificationService;

    public PhoneModel(QRCodeEntity scannedQRCodeEntity, PhoneVerificationService phoneVerificationService) {
        this.data = scannedQRCodeEntity;
        this.phoneVerificationService = phoneVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        details = PhoneEntity.builder().qrCodeId(data.getId()).build();
        // Insert into phone table
        phoneVerificationService.insertDB(details);
    }
    @Override
    public PhoneEntity getDetails () {
        return phoneVerificationService.getPhoneEntityByQRCodeId(data.getId());
    }
    @Override
    public String retrieveClassification() {
        return "";
    }
}