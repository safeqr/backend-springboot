package com.safeqr.app.gmail.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class QRCodeByContentId {
    private String cid;
    private String attachmentId;
    private List<String> decodedContent;
    private int totalQRCodeFound;
}
