
package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRCodeTypeRepository extends JpaRepository<QRCodeTypeEntity, Long> {
}
