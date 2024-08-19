package com.safeqr.app.user.entity;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name="user", schema = "safeqr")
public class UserEntity {
    @Id
    private String id;
    private String name;
    private String email;

    @Column(name = "date_created")
    private OffsetDateTime dateCreated;

    @Column(name = "date_updated")
    private OffsetDateTime dateUpdated;

    @Type(ListArrayType.class)
    @Column(name = "roles", columnDefinition = "text[]")
    private List<String> roles;
    private String status;

    @Column(name = "source")
    private String source;

    @Column(name = "gmail_history_id")
    private BigInteger gmailHistoryId = BigInteger.ZERO;
}
