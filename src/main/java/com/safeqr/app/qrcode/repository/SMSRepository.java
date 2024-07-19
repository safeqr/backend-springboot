package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.SMSEntity;

import java.util.Optional;
import java.util.UUID;

public interface SMSRepository extends GenericRepository<SMSEntity> {
    Optional<SMSEntity> findByQrCodeId(UUID qrCodeId);
}