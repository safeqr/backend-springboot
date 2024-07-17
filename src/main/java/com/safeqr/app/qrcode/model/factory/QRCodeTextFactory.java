package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.model.QRCodeText;
import com.safeqr.app.qrcode.repository.TextRepository;
import com.safeqr.app.qrcode.service.TextVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QRCodeTextFactory implements QRCodeFactory<QRCodeText> {
    @Autowired
    private TextVerificationService textVerificationService;

    @Autowired
    private TextRepository textRepository;

    @Override
    public QRCodeText create(QRCodeEntity scannedQRCodeEntity, QRCodeTypeEntity qrCodeTypeEntity) {
        return new QRCodeText(scannedQRCodeEntity, qrCodeTypeEntity, textVerificationService, textRepository);
    }
}
