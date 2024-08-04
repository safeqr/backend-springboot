package com.safeqr.app.gmail.repository;

import com.safeqr.app.gmail.entity.GmailCidEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GmailCidRespository extends JpaRepository<GmailCidEntity, UUID> {
    List<GmailCidEntity> findByGmailId(UUID gmailId);
}
