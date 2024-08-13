package com.safeqr.app.qrcodetips.repository;

import com.safeqr.app.qrcodetips.entity.QrCodeTipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QrCodeTipRepository extends JpaRepository<QrCodeTipEntity, Long> {

    @Query(value = "SELECT * FROM safeqr.qr_code_tips ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    QrCodeTipEntity findRandomTip();
}
