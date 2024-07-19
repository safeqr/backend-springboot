package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.WifiEntity;

import java.util.Optional;
import java.util.UUID;

public interface WifiRepository  extends GenericRepository<WifiEntity> {
    Optional<WifiEntity> findByQrCodeId(UUID qrCodeId);
}