package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "wifi", schema = "safeqr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WifiEntity {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @Column(name = "qr_code_id")
    private UUID qrCodeId;

    private String ssid;
    private String password;
    private String encryption;
    private boolean hidden;
}
