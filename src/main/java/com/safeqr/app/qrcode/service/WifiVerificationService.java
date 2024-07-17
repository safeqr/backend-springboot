package com.safeqr.app.qrcode.service;

import com.safeqr.app.qrcode.entity.WifiEntity;
import com.safeqr.app.qrcode.repository.WifiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WifiVerificationService {
    private final WifiRepository wifiRepository;
    private static final Logger logger = LoggerFactory.getLogger(WifiVerificationService.class);

    @Autowired
    public WifiVerificationService(WifiRepository wifiRepository) {
        this.wifiRepository = wifiRepository;
    }
    public void insertDB(WifiEntity WifiEntity) {
        wifiRepository.save(WifiEntity);
    }

}