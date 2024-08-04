package com.safeqr.app.gmail.repository;


import com.safeqr.app.gmail.entity.GmailEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GmailEmailRespository extends JpaRepository<GmailEmailEntity, UUID> {
    List<GmailEmailEntity> findByUserId(String userId);
}
