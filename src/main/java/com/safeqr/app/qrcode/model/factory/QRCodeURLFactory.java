package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.model.QRCodeURL;
import com.safeqr.app.qrcode.repository.URLRepository;
import com.safeqr.app.qrcode.service.URLVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QRCodeURLFactory implements QRCodeFactory<QRCodeURL> {
    @Autowired
    private URLVerificationService urlVerificationService;

    @Autowired
    private URLRepository urlRepository;

    @Override
    public QRCodeURL create(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity) {
        return new QRCodeURL(scannedQRCodeEntity, qrCodeTypeEntity, urlVerificationService, urlRepository);
    }
}
