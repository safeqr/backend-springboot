package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.ScanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanHistoryRepository extends JpaRepository<ScanHistory, Long> {
}