package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.model.QRCodeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class QRCodeFactoryProvider {
    private final ApplicationContext applicationContext;
    @Autowired
    public QRCodeFactoryProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public QRCodeModel createQRCodeInstance(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity) {
        return switch (qrCodeTypeEntity.getType().toUpperCase()) {
            case "URL" -> applicationContext.getBean(URLFactory.class).create(scannedQRCodeEntity, qrCodeTypeEntity);
            case "PHONE" -> applicationContext.getBean(PhoneFactory.class).create(scannedQRCodeEntity, qrCodeTypeEntity);
            case "SMS" -> applicationContext.getBean(SMSFactory.class).create(scannedQRCodeEntity, qrCodeTypeEntity);
            case "EMAIL" -> applicationContext.getBean(EmailFactory.class).create(scannedQRCodeEntity, qrCodeTypeEntity);
            case "WIFI" -> applicationContext.getBean(WifiFactory.class).create(scannedQRCodeEntity, qrCodeTypeEntity);
            case "TEXT" -> applicationContext.getBean(TextFactory.class).create(scannedQRCodeEntity, qrCodeTypeEntity);
            default -> throw new IllegalArgumentException("Unsupported QR code type: " + qrCodeTypeEntity.getType());
        };
    }
}
