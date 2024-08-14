package com.safeqr.app.qrcode.service;

import com.safeqr.app.exceptions.InvalidFormatExceptions;
import com.safeqr.app.exceptions.ResourceNotFoundExceptions;
import com.safeqr.app.qrcode.entity.EmailEntity;
import com.safeqr.app.qrcode.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new ResourceNotFoundExceptions("Email not found for QR Code id: " + qrCodeId));
    }
    public void insertDB(EmailEntity emailEntity) {
        emailRepository.save(emailEntity);
    }

    public void parseEmailString(EmailEntity emailEntity, String emailString) {
        Optional.ofNullable(emailString)
                .filter(s -> !s.isEmpty())
                .filter(s -> s.startsWith("MAILTO:"))
                .map(s -> s.substring(7))
                .map(s -> s.split("\\?", 2))
                .filter(parts -> parts.length > 0)
                .ifPresentOrElse(
                        parts -> {
                            String email = parts[0];
                            Map<String, String> params = (parts.length == 2)
                                    ? Arrays.stream(parts[1].split("&"))
                                    .map(param -> param.split("=", 2))
                                    .filter(keyValue -> keyValue.length == 2)
                                    .collect(Collectors.toMap(
                                            keyValue -> keyValue[0],
                                            keyValue -> keyValue[1],
                                            (v1, v2) -> v1
                                    ))
                                    : Map.of();

                            emailEntity.setEmail(email);
                            emailEntity.setTitle(params.getOrDefault("subject", ""));
                            emailEntity.setMessage(params.getOrDefault("body", ""));
                        },
                        () -> {
                            throw new InvalidFormatExceptions("Invalid email format. Expected format: MAILTO:<email>?subject=<title>&body=<message>");
                        }
                );
    }

}