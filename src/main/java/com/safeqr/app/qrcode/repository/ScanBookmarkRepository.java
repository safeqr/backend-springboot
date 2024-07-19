package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.ScanBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScanBookmarkRepository extends JpaRepository<ScanBookmarkEntity, Long> {
    @Query("SELECT sb.qrCodeEntity FROM ScanBookmarkEntity sb WHERE sb.userId = :userId AND sb.scanStatus = 'ACTIVE'")
    List<QRCodeEntity> findAllBookmarksByUserId(String userId);
}
