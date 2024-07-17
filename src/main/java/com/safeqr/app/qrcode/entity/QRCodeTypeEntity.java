
package com.safeqr.app.qrcode.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "qr_code_types", schema = "safeqr")
@Data
public class QRCodeTypeEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String description;

    @JsonIgnore
    private String prefix;

    @JsonIgnore
    private String tableName;
}
