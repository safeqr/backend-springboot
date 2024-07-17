package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QRCodeRepository extends JpaRepository<QRCodeEntity, UUID> {
}
