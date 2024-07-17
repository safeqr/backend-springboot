package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.TextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TextRepository extends JpaRepository<TextEntity, UUID> {
}