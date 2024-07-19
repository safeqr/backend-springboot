package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.model.TextModel;
import com.safeqr.app.qrcode.service.TextVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TextFactory implements QRCodeFactory<TextModel> {
    private final TextVerificationService textVerificationService;

    @Autowired
    public TextFactory(TextVerificationService textVerificationService) {
        this.textVerificationService = textVerificationService;
    }


    @Override
    public TextModel create(QRCodeEntity scannedQRCodeEntity) {
        return new TextModel(scannedQRCodeEntity, textVerificationService);
    }
}
