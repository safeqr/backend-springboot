package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.URLEntity;

import java.util.Optional;
import java.util.UUID;

public interface URLRepository extends GenericRepository<URLEntity> {
    Optional<URLEntity> findByQrCodeId(UUID qrCodeId);
}