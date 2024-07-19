package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.PhoneEntity;

import java.util.Optional;
import java.util.UUID;

public interface PhoneRepository extends GenericRepository<PhoneEntity> {
    Optional<PhoneEntity> findByQrCodeId(UUID qrCodeId);
}