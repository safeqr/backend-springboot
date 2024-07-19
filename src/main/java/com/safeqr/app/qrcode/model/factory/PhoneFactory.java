package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.model.PhoneModel;
import com.safeqr.app.qrcode.service.PhoneVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PhoneFactory implements QRCodeFactory<PhoneModel> {
    private final PhoneVerificationService phoneVerificationService;

    @Autowired
    public PhoneFactory(PhoneVerificationService phoneVerificationService) {
        this.phoneVerificationService = phoneVerificationService;
    }


    @Override
    public PhoneModel create(QRCodeEntity scannedQRCodeEntity) {
        return new PhoneModel(scannedQRCodeEntity, phoneVerificationService);
    }
}
