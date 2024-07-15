
package com.safeqr.app.qrcode.service;

import com.safeqr.app.constants.CommonConstants;
import com.safeqr.app.qrcode.dto.QRCodePayload;
import com.safeqr.app.qrcode.dto.response.ScanResponse;
import com.safeqr.app.qrcode.entity.QRCode;
import com.safeqr.app.qrcode.entity.QRCodeType;
import com.safeqr.app.qrcode.entity.ScanHistory;
import com.safeqr.app.qrcode.repository.QRCodeRepository;
import com.safeqr.app.qrcode.repository.QRCodeTypeRepository;
import com.safeqr.app.qrcode.repository.ScanHistoryRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
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

    public ScanResponse scanQRCode(String userId, QRCodePayload payload) {
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

        // Insert qrcode into respective table based on type
        insertIntoRespectiveTable(scannedQR);

        // Insert into Scan History table if userId is not null
        logger.info("scanQRCode: scannedQR new ID={}", scannedQR.getId());
        if (userId != null) {
            scanHistoryRepository.save(ScanHistory.builder()
                    .qrCodeId(scannedQR.getId())
                    .userId(userId)
                    .scanStatus(ScanHistory.ScanStatus.ACTIVE)
                    .build());
        }

        return ScanResponse.builder()
                .scannedQRCode(scannedQR)
                .qrCodeType(qrType)
                .build();
    }
    private QRCodeType getQRCodeType(String data) {
        return configs.stream()
                .filter(config -> data.toLowerCase().startsWith(config.getPrefix().toLowerCase()))
                .findFirst()
                .orElse(defaultQRCodeType);
    }
    private void insertIntoRespectiveTable(QRCode qrCode) {
        try {
            String url = qrCode.getContents();
            Map<String, Object> breakdown = breakdownURL(url);
            breakdown.forEach((key, value) -> logger.info("{}: {}", key, value));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
    // Function to breakdown URL into subdomain, domain, topLevelDomain, query params, fragment
    public Map<String, Object> breakdownURL(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        Map<String, Object> breakdown = new HashMap<>();

        String host = url.getHost();
        String[] hostParts = host.split("\\.");

        String subdomain = "";
        String domain = "";
        String topLevelDomain = "";

        if (hostParts.length >= 2) {
            topLevelDomain = hostParts[hostParts.length - 1];
            domain = hostParts[hostParts.length - 2];
            if (hostParts.length > 2) {
                subdomain = String.join(".", java.util.Arrays.copyOfRange(hostParts, 0, hostParts.length - 2));
            }
        }

        breakdown.put("Subdomain", subdomain.isEmpty() ? "None" : subdomain);
        breakdown.put("Domain", domain);
        breakdown.put("Top Level Domain", topLevelDomain);

        String query = url.getQuery();
        if (query != null) {
            Map<String, String> queryParams = new HashMap<>();
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                queryParams.put(pair[0], pair.length > 1 ? pair[1] : "");
            }
            breakdown.put("Query Parameters", queryParams);
        } else {
            breakdown.put("Query Parameters", "None");
        }

        String fragment = url.getRef();
        breakdown.put("Fragment", fragment != null ? fragment : "None");

        return breakdown;
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
