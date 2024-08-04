package com.safeqr.app.gmail.repository;

import com.safeqr.app.gmail.entity.GmailUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GmailUrlsRespository extends JpaRepository<GmailUrlEntity, UUID> {
    List<GmailUrlEntity> findByGmailId(UUID gmailId);
}
