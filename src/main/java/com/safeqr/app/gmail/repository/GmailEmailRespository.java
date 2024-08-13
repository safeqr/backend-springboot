package com.safeqr.app.gmail.repository;


import com.safeqr.app.gmail.entity.GmailEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface GmailEmailRespository extends JpaRepository<GmailEmailEntity, UUID> {
    // Method to find by userId and active status
    List<GmailEmailEntity> findByUserIdAndActive(String userId, Integer active);

    // Method to update active status to 0 for a specific userId
    @Modifying
    @Transactional
    @Query("UPDATE GmailEmailEntity e SET e.active = 0 WHERE e.userId = :userId")
    int deactivateEmailsByUserId(String userId);

    // Method to update active status to 0 for a specific userId and messageId
    @Modifying
    @Transactional
    @Query("UPDATE GmailEmailEntity e SET e.active = 0 WHERE e.userId = :userId AND e.messageId = :messageId")
    int deactivateEmailByUserIdAndMessageId(String userId, String messageId);
}
