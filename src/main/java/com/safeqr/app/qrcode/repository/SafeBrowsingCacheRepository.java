package com.safeqr.app.qrcode.repository;

import com.safeqr.app.qrcode.entity.SafeBrowsingCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SafeBrowsingCacheRepository extends JpaRepository<SafeBrowsingCache, UUID> {
    Optional<SafeBrowsingCache> findByHashPrefix(String hashPrefix);
}