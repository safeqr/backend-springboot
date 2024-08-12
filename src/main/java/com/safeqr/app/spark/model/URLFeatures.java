package com.safeqr.app.spark.model;

import com.safeqr.app.qrcode.entity.URLEntity;
import com.safeqr.app.qrcode.model.URLModel;
import lombok.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class URLFeatures {
    private Double domain;
    private Double subdomain;
    private Double topLevelDomain;
    private Double query;
    private Double fragment;
    private Double redirect;
    private Double path;
    private Double redirectChain;
    private Double hstsHeader;
    private Double sslStripping;
    private Double hostnameEmbedding;
    private Double javascriptCheck;
    private Double shorteningService;
    private Double hasIpAddress;
    private Double trackingDescriptions;
    private Double urlEncoding;
    private Double hasExecutable;
    private Double tls;
    private Double contents;
    private String target; // This is the label, may be null if predicting

    public static URLFeatures fromEntity(URLModel urlModel) {
        URLFeatures features = URLFeatures.builder()
                .build();
        features.setDomain(urlModel.getDetails().getDomain());
        features.setSubdomain(urlModel.getDetails().getSubdomain());
        features.setTopLevelDomain(urlModel.getDetails().getTopLevelDomain());
        features.setQuery(urlModel.getDetails().getQuery());
        features.setFragment(urlModel.getDetails().getFragment());
        features.setPath(urlModel.getDetails().getPath());
        features.setRedirectChain(urlModel.getDetails().getRedirectChain());
        features.setHstsHeader(urlModel.getDetails().getHstsHeader());
        features.setSslStripping(urlModel.getDetails().getSslStripping());
        features.setHostnameEmbedding(urlModel.getDetails().getHostnameEmbedding());
        features.setJavascriptCheck(urlModel.getDetails().getJavascriptCheck());
        features.setShorteningService(urlModel.getDetails().getShorteningService());
        features.setHasIpAddress(urlModel.getDetails().getHasIpAddress());
        features.setTrackingDescriptions(urlModel.getDetails().getTrackingDescriptions());
        features.setUrlEncoding(urlModel.getDetails().getUrlEncoding());
        features.setHasExecutable(urlModel.getDetails().getHasExecutable());
        features.setTls(Math.toIntExact(urlModel.getData().getInfo().getId()));
        features.setContents(urlModel.getData().getContents());

        return features;
    }

    // Custom setter for tls (qr_code_type_id)
    public void setTls(Integer tls) {
        if (tls != null) {
            this.tls = tls == 1 ? 0.0 : tls == 9 ? 1.0 : tls.doubleValue();
        } else {
            this.tls = 0.0;
        }
    }

    // Custom setter for hostnameEmbedding and other similar columns
    public void setHostnameEmbedding(Integer hostnameEmbedding) {
        this.hostnameEmbedding = (hostnameEmbedding != null && hostnameEmbedding != 0) ? 1.0 : 0.0;
    }

    public void setJavascriptCheck(String javascriptCheck) {
        this.javascriptCheck = (javascriptCheck != null && !javascriptCheck.isEmpty()) ? 1.0 : 0.0;
    }

    public void setShorteningService(String shorteningService) {
        this.shorteningService = (shorteningService != null && !shorteningService.isEmpty()) ? 1.0 : 0.0;
    }

    public void setHasIpAddress(String hasIpAddress) {
        this.hasIpAddress = (hasIpAddress != null && !hasIpAddress.isEmpty()) ? 1.0 : 0.0;
    }

    public void setUrlEncoding(String urlEncoding) {
        this.urlEncoding = (urlEncoding != null && !urlEncoding.isEmpty()) ? 1.0 : 0.0;
    }

    public void setHasExecutable(String hasExecutable) {
        this.hasExecutable = (hasExecutable != null && !hasExecutable.isEmpty()) ? 1.0 : 0.0;
    }

    public void setTrackingDescriptions(List<String> trackingDescriptions) {
        this.trackingDescriptions = (trackingDescriptions != null && !trackingDescriptions.isEmpty()) ? 1.0 : 0.0;
    }

    // Custom setter for sslStripping
    public void setSslStripping(List<Boolean> sslStripping) {
        if (sslStripping != null && !sslStripping.isEmpty() && sslStripping.get(0) != null) {
            this.sslStripping = sslStripping.get(0) ? 1.0 : 0.0;
        } else {
            this.sslStripping = 0.0;
        }
    }

    // Custom setter for hstsHeader
    public void setHstsHeader(List<String> hstsHeader) {
        if (hstsHeader == null || hstsHeader.isEmpty()) {
            this.hstsHeader = 0.0;
        } else if (hstsHeader.get(0).startsWith("{") && hstsHeader.get(0).endsWith("}")) {
            Pattern pattern = Pattern.compile("\"(.*?)\"");
            Matcher matcher = pattern.matcher(hstsHeader.get(0));
            if (matcher.find() && matcher.group(1).toLowerCase().contains("no")) {
                this.hstsHeader = 0.0;
            } else {
                this.hstsHeader = 1.0;
            }
        } else {
            this.hstsHeader = 1.0;
        }
    }

    // Custom setters for calculating string lengths
    public void setDomain(String domain) {
        this.domain = (domain != null) ? (double) domain.length() : 0.0;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = (subdomain != null) ? (double) subdomain.length() : 0.0;
    }

    public void setTopLevelDomain(String topLevelDomain) {
        this.topLevelDomain = (topLevelDomain != null) ? (double) topLevelDomain.length() : 0.0;
    }

    public void setQuery(String query) {
        this.query = (query != null) ? (double) query.length() : 0.0;
    }

    public void setFragment(String fragment) {
        this.fragment = (fragment != null) ? (double) fragment.length() : 0.0;
    }

    public void setPath(String path) {
        this.path = (path != null) ? (double) path.length() : 0.0;
    }

    public void setRedirectChain(List<String> redirectChain) {
        this.redirectChain = (redirectChain != null) ? (double) redirectChain.size() : 0.0;
    }

    public void setContents(String contents) {
        this.contents = (contents != null) ? (double) contents.length() : 0.0;
    }
}
