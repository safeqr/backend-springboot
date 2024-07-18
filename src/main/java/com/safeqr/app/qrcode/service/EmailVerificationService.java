package com.safeqr.app.qrcode.service;

import com.safeqr.app.qrcode.entity.EmailEntity;
import com.safeqr.app.qrcode.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {
    private final EmailRepository emailRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

    @Autowired
    public EmailVerificationService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }
    public void insertDB(EmailEntity emailEntity) {
        emailRepository.save(emailEntity);
    }

}