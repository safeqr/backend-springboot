package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.ScanHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanHistoryRepository extends JpaRepository<ScanHistoryEntity, Long> {
    @Query("SELECT sh.qrCodeEntity FROM ScanHistoryEntity sh WHERE sh.userId = :userId AND sh.scanStatus = 'ACTIVE'")
    List<QRCodeEntity> findAllQRCodesByUserId(String userId);
}