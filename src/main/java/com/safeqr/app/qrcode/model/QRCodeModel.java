package com.safeqr.app.qrcode.model;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import lombok.Data;

@Data
public abstract class QRCodeModel {
    QRCodeEntity scannedQRCode;
    QRCodeTypeEntity qrCode;

    public abstract void insertDB();
}
