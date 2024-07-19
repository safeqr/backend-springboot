package com.safeqr.app.qrcode.model.factory;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.model.QRCodeModel;

@FunctionalInterface
public interface QRCodeFactory<T extends QRCodeModel<?>> {
    T create(QRCodeEntity scannedQRCodeEntity);
}
