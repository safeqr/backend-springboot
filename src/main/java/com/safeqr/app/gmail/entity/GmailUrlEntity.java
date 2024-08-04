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
@Table(name = "gmail_urls", schema = "safeqr")
public class GmailUrlEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "gmail_id")
    private UUID gmailId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "decoded_content")
    private String decodedContent;

    @Column(name = "qr_code_id")
    private UUID qrCodeId;

}
