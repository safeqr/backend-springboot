package com.safeqr.app.user.dto;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScannedHistoriesDto {

    private QRCodeEntity data;
    private boolean bookmarked;

    public ScannedHistoriesDto(QRCodeEntity qrCodeEntity, boolean bookmarked) {
        this.data = qrCodeEntity;
        this.bookmarked = bookmarked;
    }
}