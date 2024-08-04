package com.safeqr.app.gmail.entity;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "gmail_cid", schema = "safeqr")
public class GmailCidEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "gmail_id")
    private UUID gmailId;

    @Column(name = "cid")
    private String cid;

    @Column(name = "attachment_id")
    private String attachmentId;

    @Column(name = "decoded_content")
    private String decodedContent;

    @Column(name = "qr_code_id")
    private UUID qrCodeId;
}
