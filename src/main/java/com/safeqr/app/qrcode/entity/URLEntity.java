package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String subdomain;

    private String topLevelDomain;

    private String path;

    private String query;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String fragment;

    private int redirect = 0;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Type(ListArrayType.class)
    @Column(name = "hsts_header", columnDefinition = "text[]")
    private List<String> hstsHeader = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Type(ListArrayType.class)
    @Column(name = "ssl_stripping", columnDefinition = "boolean[]")
    private List<Boolean> sslStripping = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Type(ListArrayType.class)
    @Column(name = "redirect_chain", columnDefinition = "text[]")
    private List<String> redirectChain = new ArrayList<>();

    @Column(name = "hostname_embedding")
    private Integer hostnameEmbedding = 0;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "javascript_check")
    private String javascriptCheck = "";

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "shortening_service")
    private String shorteningService = "";

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "has_ip_address")
    private String hasIpAddress = "";

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Type(ListArrayType.class)
    @Column(name = "tracking_descriptions", columnDefinition = "text[]")
    private List<String> trackingDescriptions = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "url_encoding")
    private String urlEncoding = "";

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name="has_executable")
    private String hasExecutable = "";

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "dns_error")
    private String dnsError = "";

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name="ssl_error")
    private String sslError = "";

    // Custom getter for hostnameEmbedding
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getHostnameEmbedding() {
        return hostnameEmbedding == 0 ? null : hostnameEmbedding;
    }
    // Custom getter for path
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getPath() {
        return path == null || path.isEmpty() ? null : path;
    }

    // Custom getter for query
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty
    public String getQuery() {
        return query == null || query.equals("{}") ? null : query;
    }
}
