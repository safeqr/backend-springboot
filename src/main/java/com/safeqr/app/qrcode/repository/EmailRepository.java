package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.EmailEntity;

import java.util.Optional;
import java.util.UUID;

public interface EmailRepository extends GenericRepository<EmailEntity> {
    Optional<EmailEntity> findByQrCodeId(UUID qrCodeId);
}