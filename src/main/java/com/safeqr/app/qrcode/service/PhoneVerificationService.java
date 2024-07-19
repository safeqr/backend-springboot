package com.safeqr.app.qrcode.service;

import com.safeqr.app.exceptions.CustomNotFoundExceptions;
import com.safeqr.app.qrcode.entity.PhoneEntity;
import com.safeqr.app.qrcode.repository.PhoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PhoneVerificationService {
    private final PhoneRepository phoneRepository;
    private static final Logger logger = LoggerFactory.getLogger(PhoneVerificationService.class);

    @Autowired
    public PhoneVerificationService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }
    public PhoneEntity getPhoneEntityByQRCodeId(UUID qrCodeId) {
        logger.info("qrCodeId retrieving: {}", qrCodeId);
        return phoneRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new CustomNotFoundExceptions("Phone not found for QR Code id: " + qrCodeId));
    }
    public void insertDB(PhoneEntity phoneEntity) {
        phoneRepository.save(phoneEntity);
    }

}