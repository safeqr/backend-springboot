package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.TextEntity;

import java.util.Optional;
import java.util.UUID;

public interface TextRepository extends GenericRepository<TextEntity> {
    Optional<TextEntity> findByQrCodeId(UUID qrCodeId);
}