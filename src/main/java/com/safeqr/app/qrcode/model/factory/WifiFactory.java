package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.model.WifiModel;
import com.safeqr.app.qrcode.service.WifiVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WifiFactory implements QRCodeFactory<WifiModel> {
    private final WifiVerificationService wifiVerificationService;

    @Autowired
    public WifiFactory(WifiVerificationService wifiVerificationService) {
        this.wifiVerificationService = wifiVerificationService;
    }


    @Override
    public WifiModel create(QRCodeEntity scannedQRCodeEntity) {
        return new WifiModel(scannedQRCodeEntity, wifiVerificationService);
    }
}
