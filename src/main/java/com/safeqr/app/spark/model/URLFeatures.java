package com.safeqr.app.spark.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // Custom setter for tls (qr_code_type_id)
    public void setTls(Long tls) {
        if (tls != null) {
            this.tls = tls == 1 ? 0 : tls == 9 ? 1 : tls;
        } else {
            this.tls = 0L;
        }
    }

    // Custom setter for hostnameEmbedding and other similar columns
    public void setHostnameEmbedding(Long hostnameEmbedding) {
        this.hostnameEmbedding = (hostnameEmbedding != null && hostnameEmbedding != 0) ? 1L : 0L;
    }

    public void setJavascriptCheck(Long javascriptCheck) {
        this.javascriptCheck = (javascriptCheck != null && javascriptCheck != 0) ? 1L : 0L;
    }

    public void setShorteningService(Long shorteningService) {
        this.shorteningService = (shorteningService != null && shorteningService != 0) ? 1L : 0L;
    }

    public void setHasIpAddress(Long hasIpAddress) {
        this.hasIpAddress = (hasIpAddress != null && hasIpAddress != 0) ? 1L : 0L;
    }

    public void setUrlEncoding(Long urlEncoding) {
        this.urlEncoding = (urlEncoding != null && urlEncoding != 0) ? 1L : 0L;
    }

    public void setHasExecutable(Long hasExecutable) {
        this.hasExecutable = (hasExecutable != null && hasExecutable != 0) ? 1L : 0L;
    }

    public void setTrackingDescriptions(Long trackingDescriptions) {
        this.trackingDescriptions = (trackingDescriptions != null && trackingDescriptions != 0) ? 1L : 0L;
    }

    // Custom setter for sslStripping
    public void setSslStripping(String sslStripping) {
        if (sslStripping != null && "true".equalsIgnoreCase(sslStripping)) {
            this.sslStripping = 1L;
        } else {
            this.sslStripping = 0L;
        }
    }

    // Custom setter for hstsHeader
    public void setHstsHeader(String hstsHeader) {
        if (hstsHeader == null || "0".equals(hstsHeader)) {
            this.hstsHeader = 0L;
        } else if (hstsHeader.startsWith("{") && hstsHeader.endsWith("}")) {
            Pattern pattern = Pattern.compile("\"(.*?)\"");
            Matcher matcher = pattern.matcher(hstsHeader);
            if (matcher.find() && matcher.group(1).toLowerCase().contains("no")) {
                this.hstsHeader = 0L;
            } else {
                this.hstsHeader = 1L;
            }
        } else {
            this.hstsHeader = 0L;
        }
    }

    // Custom setters for calculating string lengths
    public void setDomain(String domain) {
        this.domain = (domain != null) ? (long) domain.length() : 0L;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = (subdomain != null) ? (long) subdomain.length() : 0L;
    }

    public void setTopLevelDomain(String topLevelDomain) {
        this.topLevelDomain = (topLevelDomain != null) ? (long) topLevelDomain.length() : 0L;
    }

    public void setQuery(String query) {
        this.query = (query != null) ? (long) query.length() : 0L;
    }

    public void setFragment(String fragment) {
        this.fragment = (fragment != null) ? (long) fragment.length() : 0L;
    }

    public void setPath(String path) {
        this.path = (path != null) ? (long) path.length() : 0L;
    }

    public void setRedirectChain(String redirectChain) {
        this.redirectChain = (redirectChain != null) ? (long) redirectChain.length() : 0L;
    }

    public void setContents(String contents) {
        this.contents = (contents != null) ? (long) contents.length() : 0L;
    }
}
