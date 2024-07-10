package com.safeqr.app.qrcode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirusTotalResponse {
    private boolean safe;
    private Map<String, Object> response;
}