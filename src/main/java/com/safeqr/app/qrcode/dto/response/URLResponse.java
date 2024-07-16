package com.safeqr.app.qrcode.dto.response;

import com.safeqr.app.qrcode.entity.QRCodeURL;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public final class URLResponse extends BaseScanResponse{
    private QRCodeURL qrCodeURL;
}
