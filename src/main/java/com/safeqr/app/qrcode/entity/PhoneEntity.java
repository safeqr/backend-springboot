package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "phone", schema = "safeqr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneEntity {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @Column(name = "qr_code_id")
    private UUID qrCodeId;

    private String phone;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "remarks")
    private String remarks;
}
