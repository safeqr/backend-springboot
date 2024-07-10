
package com.safeqr.app.qrcode.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Data
@Entity
@Table(name = "safe_browsing_cache", schema = "safeqr")
public class SafeBrowsingCache {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String hashPrefix;
    private String threatType;
    private String platformType;
    private String threatEntryType;
    private String fullHash;
}
