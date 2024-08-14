package com.safeqr.app.gmail.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "gmail_emails", schema = "safeqr")
public class GmailEmailEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "thread_id")
    private String threadId;

    @Column(name = "history_id")
    private Long historyId;

    @Column(name= "subject")
    private String subject;

    @Column(name = "date_received")
    private OffsetDateTime dateReceived;

    @Column(name = "date_created")
    private OffsetDateTime dateCreated;

    @Column(name = "active")
    private int active = 1;

    @PrePersist
    public void prePersist() {
        dateCreated = OffsetDateTime.now();
    }

}
