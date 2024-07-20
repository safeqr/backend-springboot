package com.safeqr.app.qrcode.service;

import com.safeqr.app.exceptions.ResourceNotFoundExceptions;
import com.safeqr.app.qrcode.entity.SMSEntity;
import com.safeqr.app.qrcode.repository.SMSRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SMSVerificationService {
    private final SMSRepository smsRepository;
    private static final Logger logger = LoggerFactory.getLogger(SMSVerificationService.class);

    @Autowired
    public SMSVerificationService(SMSRepository smsRepository) {
        this.smsRepository = smsRepository;
    }

    public SMSEntity getSMSEntityByQRCodeId(UUID qrCodeId) {
        logger.info("qrCodeId retrieving: {}", qrCodeId);
        return smsRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundExceptions("SMS not found for QR Code id: " + qrCodeId));
    }
    public void insertDB(SMSEntity smsEntity) {
        smsRepository.save(smsEntity);
    }

}