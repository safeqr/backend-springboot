
package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.safeqr.app.constants.CommonConstants.CLASSIFY_UNKNOWN;

@Entity
@Table(name = "qr_code", schema = "safeqr")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QRCodeEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    private String userId;
    private String contents;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "qr_code_type_id", nullable = false)
    private QRCodeTypeEntity info;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "result_category")
    private String result = CLASSIFY_UNKNOWN;
}
