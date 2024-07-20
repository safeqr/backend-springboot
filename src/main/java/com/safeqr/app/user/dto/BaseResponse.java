package com.safeqr.app.user.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BaseResponse {
    private String message;
}
