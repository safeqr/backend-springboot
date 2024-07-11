package com.safeqr.app.qrcode.dto;

import lombok.Data;

@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode methods
public class QRCodePayload {
    private String data;
}