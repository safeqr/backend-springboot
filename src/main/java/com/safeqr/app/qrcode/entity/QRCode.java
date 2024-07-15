
package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "qr_code", schema = "safeqr")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QRCode {

    @Id
    @JsonIgnore
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @Column(name = "qr_code_type_id", nullable = false)
    private Long qrCodeTypeId;
    private String userId;
    private String contents;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
