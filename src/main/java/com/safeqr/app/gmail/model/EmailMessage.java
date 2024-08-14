package com.safeqr.app.gmail.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.safeqr.app.qrcode.model.QRCodeModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class EmailMessage {
    private String messageId;
    private String threadId;
    private String subject;
    private String historyId;
    private String date;
    private int active;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<QRCodeByContentId> qrCodeByContentId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<QRCodeByURL> qrCodeByURL;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<QRCodeModel<?>> decodedContentsDetails;

    public EmailMessage(String messageId, String threadId ,String subject, String historyId, String date) {
        this.messageId = messageId;
        this.threadId = threadId;
        this.subject = subject;
        this.historyId = historyId;
        this.date = date;
        this.active = 1;
        this.qrCodeByContentId = new ArrayList<>();
        this.qrCodeByURL = new ArrayList<>();
        this.decodedContentsDetails = new ArrayList<>();
    }
    public void addQRCodeByContentId(QRCodeByContentId qrCode) {
        this.qrCodeByContentId.add(qrCode);
    }

    public void addQRCodeByURL(QRCodeByURL qrCode) {
        this.qrCodeByURL.add(qrCode);
    }

    public void addQRCodeModel(QRCodeModel<?> qrCode) {
        this.decodedContentsDetails.add(qrCode);
    }

    public boolean hasQRCodes() {
        return !qrCodeByContentId.isEmpty() || !qrCodeByURL.isEmpty();
    }
}
