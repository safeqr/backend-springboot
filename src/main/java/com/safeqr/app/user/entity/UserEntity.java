package com.safeqr.app.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="user", schema = "safeqr")
public class UserEntity {
    @Id
    private String id;
    private String cognitoId;
    private String firstname;
    private String lastname;
    private String email;
    private String source;
    private String password;
    private String salt;

}
