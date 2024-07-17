package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "sms", schema = "safeqr")
@Data
@Builder
public class SMSEntity {
    @Id
    @JsonIgnore
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @Column(name = "qr_code_id")
    private UUID qrCodeId;

    private String phone;
    private String message;
}
