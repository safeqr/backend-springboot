package com.safeqr.app.prediction.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.entity.URLEntity;
import com.safeqr.app.qrcode.model.URLModel;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class URLFeaturesMapper {
    private static final Logger logger = LoggerFactory.getLogger(URLFeaturesMapper.class);

    @JsonProperty("domain")
    private Integer domain;

    @JsonProperty("subdomain")
    private Integer subdomain;

    @JsonProperty("top_level_domain")
    private Integer topLevelDomain;

    @JsonProperty("query")
    private Integer query;

    @JsonProperty("fragment")
    private Integer fragment;

    @JsonProperty("redirect")
    private Integer redirect;

    @JsonProperty("path")
    private Integer path;

    @JsonProperty("redirect_chain")
    private Integer redirectChain;

    @JsonProperty("hsts_header")
    private Integer hstsHeader;

    @JsonProperty("ssl_stripping")
    private Integer sslStripping;

    @JsonProperty("hostname_embedding")
    private Integer hostnameEmbedding;

    @JsonProperty("javascript_check")
    private Integer javascriptCheck;

    @JsonProperty("shortening_service")
    private Integer shorteningService;

    @JsonProperty("has_ip_address")
    private Integer hasIpAddress;

    @JsonProperty("tracking_descriptions")
    private Integer trackingDescriptions;

    @JsonProperty("url_encoding")
    private Integer urlEncoding;

    @JsonProperty("has_executable")
    private Integer hasExecutable;

    @JsonProperty("tls")
    private Integer tls;

    @JsonProperty("contents")
    private Integer contents;

    public static URLFeaturesMapper fromEntity(URLModel urlModel) {
        URLEntity details = urlModel.getDetails();
        QRCodeTypeEntity qrCodeTypeEntity = urlModel.getData().getInfo();
        URLFeaturesMapper features = URLFeaturesMapper.builder()
                .build();
        features.setDomain(details.getDomain());
        features.setSubdomain(details.getSubdomain());
        features.setTopLevelDomain(details.getTopLevelDomain());
        features.setQuery(details.getQuery());
        features.setFragment(details.getFragment());
        features.setPath(details.getPath());
        features.setRedirect(details.getRedirect());
        features.setRedirectChain(details.getRedirectChain());
        features.setHstsHeader(details.getHstsHeader());
        features.setSslStripping(details.getSslStripping());
        features.setHostnameEmbedding(details.getHostnameEmbedding());
        features.setJavascriptCheck(details.getJavascriptCheck());
        features.setShorteningService(details.getShorteningService());
        features.setHasIpAddress(details.getHasIpAddress());
        features.setTrackingDescriptions(details.getTrackingDescriptions());
        features.setUrlEncoding(details.getUrlEncoding());
        features.setHasExecutable(details.getHasExecutable());
        features.setTls(Math.toIntExact(qrCodeTypeEntity.getId()));
        features.setContents(urlModel.getData().getContents());

        return features;
    }

    private void setRedirect(int redirect) {
        this.redirect = redirect;
    }

    // Custom setter for tls (qr_code_type_id)
    public void setTls(Integer tls) {
        if (tls != null) {
            this.tls = tls == 1 ? 0 : tls == 9 ? 1 : tls.intValue();
        } else {
            this.tls = 0;
        }
    }

    // Custom setter for hostnameEmbedding and other similar columns
    public void setHostnameEmbedding(Integer hostnameEmbedding) {
        this.hostnameEmbedding = (hostnameEmbedding != null && hostnameEmbedding != 0) ? 1 : 0;
    }

    public void setJavascriptCheck(String javascriptCheck) {
        this.javascriptCheck = (javascriptCheck != null && !javascriptCheck.isEmpty()) ? 1 : 0;
    }

    public void setShorteningService(String shorteningService) {
        this.shorteningService = (shorteningService != null && !shorteningService.isEmpty()) ? 1 : 0;
    }

    public void setHasIpAddress(String hasIpAddress) {
        this.hasIpAddress = (hasIpAddress != null && !hasIpAddress.isEmpty()) ? 1 : 0;
    }

    public void setUrlEncoding(String urlEncoding) {
        this.urlEncoding = (urlEncoding != null && !urlEncoding.isEmpty()) ? 1 : 0;
    }

    public void setHasExecutable(String hasExecutable) {
        this.hasExecutable = (hasExecutable != null && !hasExecutable.isEmpty()) ? 1 : 0;
    }

    public void setTrackingDescriptions(List<String> trackingDescriptions) {
        this.trackingDescriptions = (trackingDescriptions != null && !trackingDescriptions.isEmpty()) ? 1 : 0;
    }

    // Custom setter for sslStripping
    public void setSslStripping(List<Boolean> sslStripping) {
        if (sslStripping != null && !sslStripping.isEmpty() && sslStripping.get(0) != null) {
            this.sslStripping = sslStripping.get(0) ? 1 : 0;
        } else {
            this.sslStripping = 0;
        }
    }

    // Custom setter for hstsHeader
    public void setHstsHeader(List<String> hstsHeader) {
        logger.info("HSTS header value: {}", hstsHeader);
        if (hstsHeader == null || hstsHeader.isEmpty()) {
            this.hstsHeader = 0;
        }  else {
            logger.info("first hsts header value: {}", hstsHeader.get(0));
            if (hstsHeader.get(0).toLowerCase().contains("no")) {
                this.hstsHeader = 0;
            } else {
                this.hstsHeader = 1;
            }
        }
    }

    // Custom setters for calculating string lengths
    public void setDomain(String domain) {
        this.domain = (domain != null) ? domain.length() : 0;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = (subdomain != null) ?  subdomain.length() : 0;
    }

    public void setTopLevelDomain(String topLevelDomain) {
        this.topLevelDomain = (topLevelDomain != null) ? topLevelDomain.length() : 0;
    }

    public void setQuery(String query) {
        this.query = (query != null) ?  query.length() : 0;
    }

    public void setFragment(String fragment) {
        this.fragment = (fragment != null) ? fragment.length() : 0;
    }

    public void setPath(String path) {
        this.path = (path != null) ? path.length() : 0;
    }

    public void setRedirectChain(List<String> redirectChain) {
        logger.info("Redirect chain: {}", redirectChain);
        if (redirectChain != null) {
            // Calculate the total number of characters in the list of strings
            int totalChars;
            totalChars = redirectChain.stream()
                    .mapToInt(String::length)
                    .sum();
            this.redirectChain = totalChars;
        } else {
            this.redirectChain = 0;
        }
    }

    public void setContents(String contents) {
        this.contents = (contents != null) ? contents.length() : 0;
    }
}
