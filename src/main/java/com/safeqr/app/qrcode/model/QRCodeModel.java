package com.safeqr.app.qrcode.model;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import lombok.Data;

@Data
public abstract class QRCodeModel<T>{
    QRCodeEntity scannedQRCode;
    T details;

    public abstract void setDetails();
    public abstract T getDetails();
}
