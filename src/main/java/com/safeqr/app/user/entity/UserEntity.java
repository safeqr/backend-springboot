package com.safeqr.app.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;
import java.time.OffsetDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Data
@Table(name="user", schema = "safeqr")
public class UserEntity {
    @Id
    private String id;
    private String name;
    private String email;
    private OffsetDateTime date_created;
    private OffsetDateTime date_updated;
    private String source;
    private String status;
}
