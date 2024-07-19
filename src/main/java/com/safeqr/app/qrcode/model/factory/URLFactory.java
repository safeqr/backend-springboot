package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.model.URLModel;
import com.safeqr.app.qrcode.service.URLVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class URLFactory implements QRCodeFactory<URLModel> {
    private final URLVerificationService urlVerificationService;

    @Autowired
    public URLFactory(URLVerificationService urlVerificationService) {
        this.urlVerificationService = urlVerificationService;
    }

    @Override
    public URLModel create(QRCodeEntity scannedQRCodeEntity) {
        return new URLModel(scannedQRCodeEntity, urlVerificationService);
    }
}
