package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmailRepository extends JpaRepository<EmailEntity, UUID> {
}