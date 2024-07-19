package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.Type;
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

    @Type(ListArrayType.class)
    @Column(name = "hsts_header", columnDefinition = "text[]")
    private List<String> hstsHeader;

    @Type(ListArrayType.class)
    @Column(name = "ssl_stripping", columnDefinition = "boolean[]")
    private List<Boolean> sslStripping;

    @Type(ListArrayType.class)
    @Column(name = "redirect_chain", columnDefinition = "text[]")
    private List<String> redirectChain;
}
