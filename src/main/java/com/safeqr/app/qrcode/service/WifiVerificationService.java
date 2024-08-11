package com.safeqr.app.qrcode.service;

import com.safeqr.app.exceptions.ResourceNotFoundExceptions;
import com.safeqr.app.qrcode.entity.WifiEntity;
import com.safeqr.app.qrcode.repository.WifiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.safeqr.app.constants.CommonConstants.*;

@Service
public class WifiVerificationService {
    private final WifiRepository wifiRepository;
    private static final Logger logger = LoggerFactory.getLogger(WifiVerificationService.class);

    @Autowired
    public WifiVerificationService(WifiRepository wifiRepository) {
        this.wifiRepository = wifiRepository;
    }
    public WifiEntity getWifiEntityByQRCodeId(UUID qrCodeId) {
        logger.info("qrCodeId retrieving: {}", qrCodeId);
        return wifiRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundExceptions("Wifi not found for QR Code id: " + qrCodeId));
    }
    public void insertDB(WifiEntity wifiEntity) {
        wifiRepository.save(wifiEntity);
    }

    public void parseWifiString(WifiEntity wifiEntity, String wifiString) {
        wifiString = wifiString.substring(5);
        // Split the string by semicolons
        String[] parts = wifiString.split(";");

        for (String part : parts) {
            if (part.startsWith("T:")) {
                wifiEntity.setEncryption(part.substring(2));
            } else if (part.startsWith("S:")) {
                wifiEntity.setSsid(part.substring(2));
            } else if (part.startsWith("P:")) {
                wifiEntity.setPassword(part.substring(2));
            } else if (part.startsWith("H:")) {
                wifiEntity.setHidden(Boolean.parseBoolean(part.substring(2)));
            }
        }

        // Unescape special characters in SSID and password
        wifiEntity.setSsid(unescapeString(wifiEntity.getSsid()));
        wifiEntity.setPassword(unescapeString(wifiEntity.getPassword()));
    }

    private String unescapeString(String input) {
        return input.replace("\\:", ":")
                .replace("\\;", ";")
                .replace("\\,", ",")
                .replace("\\\\", "\\");
    }

    public String getClassification(String encryptionType) {
        // Check if encryptionType is null
        if (encryptionType == null) {
            return CLASSIFY_UNSAFE;
        }

        if (encryptionType.equalsIgnoreCase("WPA") ||
                encryptionType.equalsIgnoreCase("WPA2") ||
                encryptionType.equalsIgnoreCase("WPA3")) {
            return CLASSIFY_SAFE;
        } else if (encryptionType.equalsIgnoreCase("WEP")) {
            return CLASSIFY_WARNING;
        } else if (encryptionType.equalsIgnoreCase("nopass")) {
            return CLASSIFY_UNSAFE;
        } else {
            return CLASSIFY_UNKNOWN;
        }
    }
}