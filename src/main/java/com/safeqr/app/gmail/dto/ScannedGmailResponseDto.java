package com.safeqr.app.gmail.dto;

import com.safeqr.app.gmail.model.EmailMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class ScannedGmailResponseDto {
    List<EmailMessage> messages;
}
