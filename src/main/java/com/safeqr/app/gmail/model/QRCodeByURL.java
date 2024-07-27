package com.safeqr.app.gmail.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class QRCodeByURL {
    private String url;
    private List<String> decodedContent;
    private int totalQRCodeFound;

}
