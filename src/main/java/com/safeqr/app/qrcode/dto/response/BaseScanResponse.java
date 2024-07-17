package com.safeqr.app.qrcode.dto.response;

import com.safeqr.app.qrcode.model.QRCodeModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseScanResponse {
    private QRCodeModel qrcode;
}
