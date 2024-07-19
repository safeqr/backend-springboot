package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.model.QRCodeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.safeqr.app.constants.CommonConstants.*;


@Component
public class QRCodeFactoryProvider {
    private final ApplicationContext applicationContext;
    @Autowired
    public QRCodeFactoryProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public QRCodeModel createQRCodeInstance(QRCodeEntity scannedQRCodeEntity) {
        return switch (scannedQRCodeEntity.getInfo().getType().toUpperCase()) {
            case QR_CODE_TYPE_URL-> applicationContext.getBean(URLFactory.class).create(scannedQRCodeEntity);
            case QR_CODE_TYPE_PHONE -> applicationContext.getBean(PhoneFactory.class).create(scannedQRCodeEntity);
            case QR_CODE_TYPE_SMS -> applicationContext.getBean(SMSFactory.class).create(scannedQRCodeEntity);
            case QR_CODE_TYPE_EMAIL -> applicationContext.getBean(EmailFactory.class).create(scannedQRCodeEntity);
            case QR_CODE_TYPE_WIFI -> applicationContext.getBean(WifiFactory.class).create(scannedQRCodeEntity);
            case DEFAULT_QR_CODE_TYPE -> applicationContext.getBean(TextFactory.class).create(scannedQRCodeEntity);
            default -> throw new IllegalArgumentException("Unsupported QR code type: " + scannedQRCodeEntity.getInfo().getType());
        };
    }
}
