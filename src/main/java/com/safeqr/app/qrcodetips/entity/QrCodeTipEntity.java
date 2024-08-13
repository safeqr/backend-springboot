package com.safeqr.app.qrcodetips.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qr_code_tips", schema = "safeqr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeTipEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tips;
}

