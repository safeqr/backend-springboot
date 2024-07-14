package com.safeqr.app.qrcode.entity;

import jakarta.persistence.*;
import lombok.Builder;

import java.util.UUID;

@Entity
@Builder
@Table(name = "scan_history", schema = "safeqr")
public class ScanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qr_code_id", nullable = false)
    private UUID qrCodeId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ScanStatus scanStatus;

    public enum ScanStatus {
        ACTIVE,
        INACTIVE
    }
}