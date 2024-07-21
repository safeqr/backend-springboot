package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.ScanHistoryEntity;
import com.safeqr.app.user.dto.ScannedHistoriesDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScanHistoryRepository extends JpaRepository<ScanHistoryEntity, Long> {
    @Query("SELECT new com.safeqr.app.user.dto.ScannedHistoriesDto(sh.qrCodeEntity, sh.bookmarked) " +
            "FROM ScanHistoryEntity sh WHERE sh.userId = :userId AND sh.scanStatus = 'ACTIVE' ORDER BY sh.dateCreated DESC")
    List<ScannedHistoriesDto> findAllQRCodesByUserId(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE ScanHistoryEntity sh SET sh.scanStatus = com.safeqr.app.qrcode.entity.ScanHistoryEntity$ScanStatus.INACTIVE, sh.dateUpdated = CURRENT_TIMESTAMP WHERE sh.userId = :userId AND sh.scanStatus = com.safeqr.app.qrcode.entity.ScanHistoryEntity$ScanStatus.ACTIVE AND sh.qrCodeId = :qrCodeId")
    int updateScannedHistoryToInactive(String userId, UUID qrCodeId);

    @Modifying
    @Query("UPDATE ScanHistoryEntity sh SET sh.scanStatus = com.safeqr.app.qrcode.entity.ScanHistoryEntity$ScanStatus.INACTIVE, sh.dateUpdated = CURRENT_TIMESTAMP WHERE sh.userId = :userId AND sh.scanStatus = com.safeqr.app.qrcode.entity.ScanHistoryEntity$ScanStatus.ACTIVE")
    int updateScannedHistoriesToInactiveByUserId(String userId);
}