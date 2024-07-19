package com.safeqr.app.qrcode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scan_bookmark", schema = "safeqr")
public class ScanBookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qr_code_id", nullable = false)
    private UUID qrCodeId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookmarkStatus scanStatus;

    public enum BookmarkStatus {
        ACTIVE,
        INACTIVE
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_code_id", referencedColumnName = "id", insertable = false, updatable = false)
    private QRCodeEntity qrCodeEntity;
}