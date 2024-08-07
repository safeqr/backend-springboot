package com.safeqr.app.qrcode.model;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import lombok.Data;

@Data
public abstract class QRCodeModel<T>{
    QRCodeEntity data;
    T details;

    public abstract void setDetails();
    public abstract T getDetails();
    public abstract String retrieveClassification();
}
