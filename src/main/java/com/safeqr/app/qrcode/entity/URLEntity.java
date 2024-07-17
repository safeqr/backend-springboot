package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "url", schema = "safeqr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class URLEntity {
    @Id
    @JsonIgnore
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @Column(name = "qr_code_id")
    private UUID qrCodeId;

    private String domain;

    private String subdomain;

    private String topLevelDomain;

    private String path;

    @JsonProperty
    private String query;

    private String fragment;

    private int redirect = 0;

    @Column(name = "redirect_chain")
    private List<String> redirectChain;
}
