
package com.safeqr.app.qrcode.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "qr_code_types", schema = "safeqr")
@Data
public class QRCodeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String description;
    private String prefix;
    private String tableName;
}
