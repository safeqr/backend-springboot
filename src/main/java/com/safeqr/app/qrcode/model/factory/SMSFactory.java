package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.model.SMSModel;
import com.safeqr.app.qrcode.service.SMSVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSFactory implements QRCodeFactory<SMSModel> {
    private final SMSVerificationService smsVerificationService;

    @Autowired
    public SMSFactory(SMSVerificationService smsVerificationService) {
        this.smsVerificationService = smsVerificationService;
    }


    @Override
    public SMSModel create(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity) {
        return new SMSModel(scannedQRCodeEntity, qrCodeTypeEntity, smsVerificationService);
    }
}
