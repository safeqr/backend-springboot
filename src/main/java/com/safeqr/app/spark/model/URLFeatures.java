package com.safeqr.app.spark.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class URLFeatures {
    private Long domain;
    private Long subdomain;
    private Long topLevelDomain;
    private Long query;
    private Long fragment;
    private Long redirect;
    private Long path;
    private Long redirectChain;
    private Long hstsHeader;
    private Long sslStripping;
    private Long hostnameEmbedding;
    private Long javascriptCheck;
    private Long shorteningService;
    private Long hasIpAddress;
    private Long trackingDescriptions;
    private Long urlEncoding;
    private Long hasExecutable;
    private Long tls;
    private Long contents;
    private String target; // This is the label, may be null if predicting
}
