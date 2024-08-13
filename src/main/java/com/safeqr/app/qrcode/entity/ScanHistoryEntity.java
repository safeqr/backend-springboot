package com.safeqr.app.qrcode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scan_history", schema = "safeqr")
public class ScanHistoryEntity {

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

    @Column(name = "date_created", updatable = false)
    private OffsetDateTime dateCreated;

    @Column(name = "date_updated")
    private OffsetDateTime dateUpdated;

    @Column(name = "bookmarked")
    private boolean bookmarked;

    public enum ScanStatus {
        ACTIVE,
        INACTIVE
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_code_id", referencedColumnName = "id", insertable = false, updatable = false)
    private QRCodeEntity qrCodeEntity;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        dateCreated = now;
        dateUpdated = now;
    }
    @PreUpdate
    public void preUpdate() {
        dateUpdated = OffsetDateTime.now();
    }

}