package com.safeqr.app.exceptions;

public class CustomNotFoundExceptions extends RuntimeException {
    public CustomNotFoundExceptions(String message){
        super(message);
    }
}
