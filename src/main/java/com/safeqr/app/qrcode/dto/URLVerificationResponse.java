package com.safeqr.app.qrcode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class URLVerificationResponse {
    private boolean secure;
    private String message;
}