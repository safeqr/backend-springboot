
package com.safeqr.app.qrcode.service;

import com.safeqr.app.constants.CommonConstants;
import com.safeqr.app.qrcode.dto.QRCodePayload;
import com.safeqr.app.qrcode.dto.response.BaseScanResponse;
import com.safeqr.app.qrcode.dto.response.URLResponse;
import com.safeqr.app.qrcode.entity.QRCode;
import com.safeqr.app.qrcode.entity.QRCodeType;
import com.safeqr.app.qrcode.entity.QRCodeURL;
import com.safeqr.app.qrcode.entity.ScanHistory;
import com.safeqr.app.qrcode.repository.QRCodeRepository;
import com.safeqr.app.qrcode.repository.QRCodeTypeRepository;
import com.safeqr.app.qrcode.repository.ScanHistoryRepository;
import com.safeqr.app.qrcode.repository.URLRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;

@Service
public class QRCodeTypeService {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeTypeService.class);

    @Autowired
    private QRCodeTypeRepository qrCodeTypeRepository;
    @Autowired
    private ScanHistoryRepository scanHistoryRepository;
    @Autowired
    private QRCodeRepository qrCodeRepository;
    @Autowired
    private URLRepository urlRepository;

    @Autowired
    private SafeBrowsingService safeBrowsingService;

    private List<QRCodeType> configs;
    private QRCodeType defaultQRCodeType;

    @PostConstruct
    public void loadQRCodeTypes() {
        // Fetch all QR Code Types from the database
        configs = qrCodeTypeRepository.findAll();
        // Set the default QR Code Type
        defaultQRCodeType = configs.stream()
                .filter(config -> config.getType().equals(CommonConstants.DEFAULT_QR_CODE_TYPE))
                .findFirst()
                .orElse(null);
    }

    public List<QRCodeType> getAllTypes() {
        return configs;
    }

    public BaseScanResponse scanQRCode(String userId, QRCodePayload payload) {
        String data = payload.getData();
        logger.info("scanQRCode: userId={}, data={}", userId, data);

        // Get the QR Code Type
        QRCodeType qrType = getQRCodeType(data);

        // Insert the QR Code into main qrcode table
        QRCode scannedQR = qrCodeRepository.save(QRCode.builder()
                .userId(userId)
                .contents(data)
                .qrCodeTypeId(qrType.getId())
                .createdAt(LocalDateTime.now())
                .build());

        // Insert into Scan History table if userId is not null
        logger.info("scanQRCode: scannedQR new ID={}", scannedQR.getId());
        if (userId != null) {
            scanHistoryRepository.save(ScanHistory.builder()
                    .qrCodeId(scannedQR.getId())
                    .userId(userId)
                    .scanStatus(ScanHistory.ScanStatus.ACTIVE)
                    .build());
        }
        // Insert into various tables
        return insertIntoRespectiveTable(scannedQR, qrType);
    }
    private QRCodeType getQRCodeType(String data) {
        return configs.stream()
                .filter(config -> data.toLowerCase().startsWith(config.getPrefix().toLowerCase()))
                .findFirst()
                .orElse(defaultQRCodeType);
    }
    private BaseScanResponse insertIntoRespectiveTable(QRCode qrCode, QRCodeType qrCodeType) {
        try {
            String url = qrCode.getContents();
            QRCodeURL urlObj = breakdownURL(url);
            urlObj.setQrCodeId(qrCode.getId());
            List<String> redirectChain = countAndTrackRedirects(url);
            logger.info("Redirect chain: {}", redirectChain);
            for (int i = 0; i < redirectChain.size(); i++) {
                logger.info((i == 0 ? "Initial URL: " : "Redirect #" + i + ": ") + redirectChain.get(i));
            }
            urlObj.setRedirect(redirectChain.size() - 1);
            urlObj.setRedirectChain(redirectChain);
            urlRepository.save(urlObj);

            return URLResponse.builder()
                    .scannedQRCode(qrCode)
                    .qrCodeType(qrCodeType)
                    .qrCodeURL(urlObj)
                    .build();
        } catch (IOException e) {
            logger.error("Error: ", e);
        }

        return BaseScanResponse.builder()
                .scannedQRCode(qrCode)
                .qrCodeType(qrCodeType)
                .build();
    }
    // Function to breakdown URL into subdomain, domain, topLevelDomain, query params, fragment
    public QRCodeURL breakdownURL(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        QRCodeURL urlObj = new QRCodeURL();

        String host = url.getHost();
        String[] hostParts = host.split("\\.");
        String subdomain = "";

        if (hostParts.length >= 2) {
            urlObj.setTopLevelDomain(hostParts[hostParts.length - 1]);
            urlObj.setDomain(hostParts[hostParts.length - 2]);
            if (hostParts.length > 2) {
                subdomain = String.join(".", java.util.Arrays.copyOfRange(hostParts, 0, hostParts.length - 2));
            }
        }

        urlObj.setSubdomain(subdomain);

        String path = url.getPath();
        urlObj.setPath(path.isEmpty() ? "/" : path);

        String query = url.getQuery();
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                queryParams.put(pair[0], pair.length > 1 ? pair[1] : "");
            }
            logger.info("queryParams: {}", queryParams);
        }
        urlObj.setQuery(queryParams.toString());

        String fragment = url.getRef();

        urlObj.setFragment(fragment);

        return urlObj;
    }

    List<String> countAndTrackRedirects(String urlString) throws IOException {
        URL url = new URL(urlString);
        List<String> redirectChain = new ArrayList<>();
        redirectChain.add(urlString); // Add the initial URL to the chain
        boolean redirected;
        int redirectCount = 0;

        do {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);

            int responseCode = connection.getResponseCode();
            redirected = (responseCode >= 300 && responseCode < 400);

            if (redirected) {
                String newUrl = connection.getHeaderField("Location");
                if (newUrl == null) {
                    break;
                }
                // Handle relative URLs
                if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
                    newUrl = new URL(url, newUrl).toString();
                }
                url = new URL(newUrl);
                redirectChain.add(newUrl);
                redirectCount++;
                logger.info("Redirect #{}: {}",redirectCount, newUrl);
            }

            connection.disconnect();
        } while (redirected && redirectCount < CommonConstants.MAX_REDIRECT_COUNT);

        return redirectChain;
    }

    public Mono<String> detectType(QRCodePayload payload) {
        String data = payload.getData();

        for (QRCodeType config : configs) {
            if (data.startsWith(config.getPrefix())) {
                if ("URL".equals(config.getType())) {
                    try
                    {
                        return safeBrowsingService.isSafeUrl(data)
                                .map(isSafe -> isSafe ? "Safe URL" : "Unsafe URL");
                    } catch (NoSuchAlgorithmException e)
                    {
                        // TODO Auto-generated catch block
                        return Mono.just("Error checking URL safety: " + e.getMessage());
                    }
                }
                return Mono.just(config.getType());
            }
        }

        return Mono.just("Unknown");
    }
}
