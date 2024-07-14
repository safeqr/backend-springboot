package com.safeqr.app.qrcode.dto.response;

import com.safeqr.app.qrcode.entity.QRCodeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScanResponse {
    private String contents;
    private String qrType;
}
