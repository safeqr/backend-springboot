package com.safeqr.app.qrcode.service;

import com.safeqr.app.exceptions.CustomNotFoundExceptions;
import com.safeqr.app.qrcode.entity.EmailEntity;
import com.safeqr.app.qrcode.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailVerificationService {
    private final EmailRepository emailRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

    @Autowired
    public EmailVerificationService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }
    public EmailEntity getEmailEntityByQRCodeId(UUID qrCodeId) {
        logger.info("qrCodeId retrieving: {}", qrCodeId);
        return emailRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new CustomNotFoundExceptions("Email not found for QR Code id: " + qrCodeId));
    }
    public void insertDB(EmailEntity emailEntity) {
        emailRepository.save(emailEntity);
    }

}