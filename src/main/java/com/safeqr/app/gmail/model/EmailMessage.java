package com.safeqr.app.gmail.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class EmailMessage {
    private String messageId;
    private String subject;
    private String historyId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<QRCodeByContentId> qrCodeByContentId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<QRCodeByURL> qrCodeByURL;

    public EmailMessage(String messageId, String subject, String historyId) {
        this.messageId = messageId;
        this.subject = subject;
        this.historyId = historyId;
        this.qrCodeByContentId = new ArrayList<>();
        this.qrCodeByURL = new ArrayList<>();
    }
    public void addQRCodeByContentId(QRCodeByContentId qrCode) {
        this.qrCodeByContentId.add(qrCode);
    }

    public void addQRCodeByURL(QRCodeByURL qrCode) {
        this.qrCodeByURL.add(qrCode);
    }

    public boolean hasQRCodes() {
        return !qrCodeByContentId.isEmpty() || !qrCodeByURL.isEmpty();
    }
}
