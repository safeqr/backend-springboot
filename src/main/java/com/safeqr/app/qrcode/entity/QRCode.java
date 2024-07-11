
package com.safeqr.app.qrcode.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "qr_code", schema = "safeqr")
@Data
public class QRCode {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "qr_code_type_id", nullable = false)
    private QRCodeType qrCodeType;

    private String userId;
    private String contents;

    @Column(name = "created_at", insertable = false, updatable = false)
    private String createdAt;
}
