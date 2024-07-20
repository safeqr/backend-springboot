package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.ScanBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScanBookmarkRepository extends JpaRepository<ScanBookmarkEntity, Long> {
    @Query("SELECT sb.qrCodeEntity FROM ScanBookmarkEntity sb WHERE sb.userId = :userId AND sb.bookmarkStatus = 'ACTIVE'")
    List<QRCodeEntity> findAllBookmarksByUserId(String userId);

    @Query("SELECT sb FROM ScanBookmarkEntity sb WHERE sb.userId = :userId AND sb.qrCodeId = :qrCodeId AND sb.bookmarkStatus = 'ACTIVE'")
    Optional<ScanBookmarkEntity> findByUserIdAndQrCodeId(String userId, UUID qrCodeId);

    @Modifying
    @Query("UPDATE ScanBookmarkEntity sb SET sb.bookmarkStatus = com.safeqr.app.qrcode.entity.ScanBookmarkEntity$BookmarkStatus.INACTIVE, sb.dateUpdated = CURRENT_TIMESTAMP WHERE sb.userId = :userId AND sb.bookmarkStatus = com.safeqr.app.qrcode.entity.ScanBookmarkEntity$BookmarkStatus.ACTIVE AND sb.qrCodeId = :qrCodeId")
    int updateBookmarkStatusToInactive(String userId, UUID qrCodeId);

    @Modifying
    @Query("UPDATE ScanBookmarkEntity sb SET sb.bookmarkStatus = com.safeqr.app.qrcode.entity.ScanBookmarkEntity$BookmarkStatus.INACTIVE, sb.dateUpdated = CURRENT_TIMESTAMP WHERE sb.userId = :userId AND sb.bookmarkStatus = com.safeqr.app.qrcode.entity.ScanBookmarkEntity$BookmarkStatus.ACTIVE")
    int updateBookmarkStatusToInactiveByUserId(String userId);
}
