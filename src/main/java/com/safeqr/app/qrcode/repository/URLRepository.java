package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.QRCodeURLEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface URLRepository extends JpaRepository<QRCodeURLEntity, UUID> {
}