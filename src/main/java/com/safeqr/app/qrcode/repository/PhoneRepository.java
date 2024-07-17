package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.PhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PhoneRepository extends JpaRepository<PhoneEntity, UUID> {
}