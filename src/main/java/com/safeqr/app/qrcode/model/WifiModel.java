package com.safeqr.app.qrcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.WifiEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.service.WifiVerificationService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public class WifiModel extends QRCodeModel {
    private static final Logger logger = LoggerFactory.getLogger(WifiModel.class);

    @JsonIgnore
    private final WifiVerificationService wifiVerificationService;

    WifiEntity details;

    public WifiModel(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity, WifiVerificationService wifiVerificationService) {
        this.scannedQRCode = scannedQRCodeEntity;
        this.qrCode = qrCodeTypeEntity;
        this.wifiVerificationService = wifiVerificationService;
        this.details = null;
    }

    @Override
    public void setDetails() {
        details = WifiEntity.builder().qrCodeId(scannedQRCode.getId()).build();
        // Insert into wifi table
        wifiVerificationService.insertDB(details);
    }
}