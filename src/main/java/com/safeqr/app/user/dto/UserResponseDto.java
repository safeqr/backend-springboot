package com.safeqr.app.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class UserResponseDto {
    private String id;
    private String name;
    private String email;
    private OffsetDateTime dateJoined;
    private OffsetDateTime dateUpdated;
    private List<String> roles;
    private String status;
}
