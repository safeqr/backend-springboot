package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.ScanHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanHistoryRepository extends JpaRepository<ScanHistoryEntity, Long> {
}