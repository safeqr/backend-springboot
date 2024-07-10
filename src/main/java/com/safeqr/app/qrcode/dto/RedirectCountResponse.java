package com.safeqr.app.qrcode.dto;

import lombok.Data;

@Data
public class RedirectCountResponse {
    private int redirectCount;
    private String message;
}