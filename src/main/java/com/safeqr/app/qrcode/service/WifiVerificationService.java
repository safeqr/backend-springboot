package com.safeqr.app.qrcode.service;

import com.safeqr.app.exceptions.ResourceNotFoundExceptions;
import com.safeqr.app.qrcode.entity.WifiEntity;
import com.safeqr.app.qrcode.repository.WifiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

}