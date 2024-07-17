package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.SMSEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SMSRepository extends JpaRepository<SMSEntity, UUID> {
}