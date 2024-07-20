package com.safeqr.app.qrcode.service;

import static com.safeqr.app.constants.CommonConstants.*;

import com.safeqr.app.exceptions.ResourceNotFoundExceptions;
import com.safeqr.app.qrcode.dto.request.QRCodePayload;
import com.safeqr.app.qrcode.dto.URLVerificationResponse;
import com.safeqr.app.qrcode.entity.URLEntity;
import com.safeqr.app.qrcode.repository.URLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.*;
import java.util.*;


@Service
public class URLVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(URLVerificationService.class);
    private final URLRepository urlRepository;
    @Autowired
    public URLVerificationService(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public URLEntity getURLEntityByQRCodeId(UUID qrCodeId) {
        logger.info("qrCodeId retrieving: {}", qrCodeId);
        return urlRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundExceptions("URL not found for QR Code id: " + qrCodeId));
    }

    public void insertDB(URLEntity urlEntity) {
        urlRepository.save(urlEntity);
    }
    // Function to breakdown URL into subdomain, domain, topLevelDomain, query params, fragment
    public URLEntity breakdownURL(String urlString) throws MalformedURLException, URISyntaxException {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        URLEntity urlObj = new URLEntity();

        String host = url.getHost();
        // split host into subdomain, domain, topLevelDomain
        String[] hostParts = host.split("\\.");
        String subdomain = "";

        if (hostParts.length >= 2) {
            // set topLevelDomain to the last part of the host
            urlObj.setTopLevelDomain(hostParts[hostParts.length - 1]);
            // set domain to the second last part of the host
            urlObj.setDomain(hostParts[hostParts.length - 2]);
            // set subdomain to the first part of the host
            if (hostParts.length > 2) {
                subdomain = String.join(".", java.util.Arrays.copyOfRange(hostParts, 0, hostParts.length - 2));
            }
        }
        // set subdomain to URL host
        urlObj.setSubdomain(subdomain);

        String path = url.getPath();
        //set path to URL path if it's not empty, otherwise set it to root path
        urlObj.setPath(path.isEmpty() ? "/" : path);

        String query = url.getQuery();
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            // split query params into key value pairs
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                queryParams.put(pair[0], pair.length > 1 ? pair[1] : "");
            }
            logger.info("queryParams: {}", queryParams);
        }
        // set query params to URL query
        urlObj.setQuery(queryParams.toString());
        // set fragment to URL ref
        urlObj.setFragment(Optional.ofNullable(url.getRef()).orElse(""));

        return urlObj;
    }

    public void countAndTrackRedirects(String urlString, URLEntity details) throws IOException, URISyntaxException {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        List<String> redirectChain = new ArrayList<>();
        List<String> hstsHeaderList = new ArrayList<>();
        List<Boolean> sslStrippingList = new ArrayList<>();

        // Add the initial URL to the chain
        redirectChain.add(urlString);
        boolean redirected;
        int redirectCount = 0;

        do {
            URLConnection testConnection = url.openConnection();

            if (!(testConnection instanceof HttpURLConnection)) {
                // Handle non-HTTP connections (like mailto:)
                logger.info("Non-HTTP URL encountered: {}", url);
                hstsHeaderList.add(INFO_HSTS_NOT_APPLICABLE);
                sslStrippingList.add(false);
                break;
            }
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);

            int responseCode = connection.getResponseCode();
            redirected = (responseCode >= 300 && responseCode < 400);

            // Checks for HSTS Header
            hstsHeaderList.add(detectHSTSHeader(url, connection));

            // Handle redirects
            if (redirected) {
                // Location header contains the URL to redirect to
                String newUrl = connection.getHeaderField("Location");
                if (newUrl == null) {
                    break;
                }
                URI newUri = uri.resolve(newUrl);
                // check for SSL stripping during redirect
                sslStrippingList.add(checkRedirectForSSLStripping(uri, newUri));

                // Handle relative URLs
                uri = uri.resolve(newUrl);
                url = uri.toURL();
                redirectChain.add(url.toString());
                redirectCount++;
                logger.info("Redirect #{}: {}",redirectCount, newUrl);
            } else {
                // No redirect, so no SSL stripping
                sslStrippingList.add(false);
            }

            connection.disconnect();
        } while (redirected && redirectCount < MAX_REDIRECT_COUNT);

        details.setRedirect(redirectChain.size() - 1);
        details.setRedirectChain(redirectChain);
        details.setSslStripping(sslStrippingList);
        details.setHstsHeader(hstsHeaderList);
    }
    // Function to check if the redirect is from HTTPS to HTTP
    private boolean checkRedirectForSSLStripping(URI originalUri, URI newUri) {
        return originalUri.getScheme().equalsIgnoreCase("https") &&
                newUri.getScheme().equalsIgnoreCase("http");
    }
    // Function to check if HSTS header is present for HTTPS connections
    private String detectHSTSHeader(URL url, HttpURLConnection connection) {
        if (connection instanceof HttpsURLConnection) {
            String hstsHeader = connection.getHeaderField("Strict-Transport-Security");
            if (hstsHeader != null && !hstsHeader.isEmpty()) {
                logger.info("HSTS Header detected for {}: {}", url, hstsHeader);
                return INFO_HSTS_HEADER_PREFIX + hstsHeader;
            } else {
                logger.warn("No HSTS Header for HTTPS connection to {}", url);
                return INFO_NO_HSTS_HEADER;
            }
        }
        return INFO_NON_SECURE_CONNECTION;
    }

    public URLVerificationResponse verifyURL(QRCodePayload payload) {
        URLVerificationResponse response = new URLVerificationResponse();
        try {
            java.net.URL url = new java.net.URL(payload.getData());
            String protocol = url.getProtocol();
            if ("https".equalsIgnoreCase(protocol)) {
                response.setSecure(true);
                response.setMessage("The connection is secure.");
            } else {
                response.setSecure(false);
                response.setMessage("The connection is not secure.");
            }
        } catch (Exception e) {
            response.setSecure(false);
            response.setMessage("Invalid URL.");
        }
        return response;
    }
}