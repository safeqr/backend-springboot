package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.URLEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface URLRepository extends GenericRepository<URLEntity> {
    @Transactional
    Optional<URLEntity> findByQrCodeId(UUID qrCodeId);
}