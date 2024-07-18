package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.model.EmailModel;
import com.safeqr.app.qrcode.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailFactory implements QRCodeFactory<EmailModel> {
    private final EmailVerificationService emailVerificationService;

    @Autowired
    public EmailFactory(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }


    @Override
    public EmailModel create(QRCodeEntity scannedQRCodeEntity) {
        return new EmailModel(scannedQRCodeEntity, emailVerificationService);
    }
}
