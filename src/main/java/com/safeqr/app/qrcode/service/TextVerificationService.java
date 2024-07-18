package com.safeqr.app.qrcode.service;

import com.safeqr.app.qrcode.entity.TextEntity;
import com.safeqr.app.qrcode.repository.TextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TextVerificationService {
    private final TextRepository textRepository;
    private static final Logger logger = LoggerFactory.getLogger(TextVerificationService.class);
    @Autowired
    public TextVerificationService(TextRepository textRepository) {
        this.textRepository = textRepository;
    }
    public void insertDB(TextEntity textEntity) {
        textRepository.save(textEntity);
    }

}