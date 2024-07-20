package com.safeqr.app.user.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BookmarkRequestDto {
    private UUID qrCodeId;
}
