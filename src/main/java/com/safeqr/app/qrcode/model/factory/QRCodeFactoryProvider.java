package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.model.QRCodeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class QRCodeFactoryProvider {
    @Autowired
    private ApplicationContext applicationContext;

    public QRCodeModel createQRCodeInstance(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity) {
        switch (qrCodeTypeEntity.getType().toUpperCase()) {
            case "URL":
                return applicationContext.getBean(QRCodeURLFactory.class).create(scannedQRCodeEntity, qrCodeTypeEntity);
            case "TEXT":
                return applicationContext.getBean(QRCodeTextFactory.class).create(scannedQRCodeEntity, qrCodeTypeEntity);
            default:
                throw new IllegalArgumentException("Unsupported QR code type: " + qrCodeTypeEntity.getType());
        }
    }
}
