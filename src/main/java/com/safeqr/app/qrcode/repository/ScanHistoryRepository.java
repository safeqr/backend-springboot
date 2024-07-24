package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.ScanBookmarkEntity;
import com.safeqr.app.qrcode.entity.ScanHistoryEntity;
import com.safeqr.app.user.dto.ScannedHistoriesDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScanHistoryRepository extends JpaRepository<ScanHistoryEntity, Long> {
    @Query("SELECT new com.safeqr.app.user.dto.ScannedHistoriesDto(sh.qrCodeEntity, sh.bookmarked) FROM ScanHistoryEntity sh WHERE sh.userId = :userId AND sh.scanStatus = 'ACTIVE' ORDER BY sh.dateCreated DESC")
    List<ScannedHistoriesDto> findAllQRCodesByUserId(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE ScanHistoryEntity sh SET sh.scanStatus = com.safeqr.app.qrcode.entity.ScanHistoryEntity$ScanStatus.INACTIVE, sh.dateUpdated = CURRENT_TIMESTAMP WHERE sh.userId = :userId AND sh.scanStatus = com.safeqr.app.qrcode.entity.ScanHistoryEntity$ScanStatus.ACTIVE AND sh.qrCodeId = :qrCodeId")
    int updateScannedHistoryToInactive(String userId, UUID qrCodeId);

    @Modifying
    @Query("UPDATE ScanHistoryEntity sh SET sh.scanStatus = com.safeqr.app.qrcode.entity.ScanHistoryEntity$ScanStatus.INACTIVE, sh.dateUpdated = CURRENT_TIMESTAMP WHERE sh.userId = :userId AND sh.scanStatus = com.safeqr.app.qrcode.entity.ScanHistoryEntity$ScanStatus.ACTIVE")
    int updateScannedHistoriesToInactiveByUserId(String userId);
    @Query("SELECT sh FROM ScanHistoryEntity sh WHERE sh.userId = :userId AND sh.qrCodeId = :qrCodeId AND sh.bookmarked = true AND sh.scanStatus = 'ACTIVE'")
    Optional<ScanHistoryEntity> findByUserIdAndQrCodeId(String userId, UUID qrCodeId);

    @Query("SELECT new com.safeqr.app.user.dto.ScannedHistoriesDto(sh.qrCodeEntity, sh.bookmarked) FROM ScanHistoryEntity sh WHERE sh.userId = :userId AND sh.bookmarked = true AND sh.scanStatus = 'ACTIVE' ORDER BY sh.dateCreated DESC")
    List<ScannedHistoriesDto> findAllBookmarksByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE ScanHistoryEntity sh SET sh.bookmarked = true, sh.dateUpdated = CURRENT_TIMESTAMP " +
            "WHERE sh.userId = :userId AND sh.bookmarked = false AND sh.qrCodeId = :qrCodeId AND sh.scanStatus = 'ACTIVE'")
    int updateBookmarkStatusToActive(String userId, UUID qrCodeId);
    @Modifying
    @Transactional
    @Query("UPDATE ScanHistoryEntity sh SET sh.bookmarked = false, sh.dateUpdated = CURRENT_TIMESTAMP " +
            "WHERE sh.userId = :userId AND sh.bookmarked = true AND sh.qrCodeId = :qrCodeId AND sh.scanStatus = 'ACTIVE'")
    int updateBookmarkStatusToInactive(String userId, UUID qrCodeId);

    @Modifying
    @Transactional
    @Query("UPDATE ScanHistoryEntity sh SET sh.bookmarked = false, sh.dateUpdated = CURRENT_TIMESTAMP WHERE sh.userId = :userId AND sh.bookmarked = true AND sh.scanStatus = 'ACTIVE'")
    int updateBookmarkStatusToInactiveByUserId(String userId);
}