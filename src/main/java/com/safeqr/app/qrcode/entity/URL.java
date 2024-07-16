package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "url", schema = "safeqr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class URL {

    @Id
    @JsonIgnore
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "qr_code_id")
    private UUID qrCodeId;

    private String domain;

    private String subdomain;

    private String topLevelDomain;

    private String query;

    private String fragment;

    private int redirect = 0;
}
