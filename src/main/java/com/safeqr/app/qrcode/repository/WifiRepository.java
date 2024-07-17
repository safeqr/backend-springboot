package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.WifiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WifiRepository extends JpaRepository<WifiEntity, UUID> {
}