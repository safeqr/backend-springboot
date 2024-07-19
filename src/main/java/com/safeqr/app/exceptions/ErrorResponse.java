package com.safeqr.app.exceptions;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponse {
    private String error;
    private int status;

    public ErrorResponse(String message, int status){
        this.error = message;
        this.status = status;
    }
}
