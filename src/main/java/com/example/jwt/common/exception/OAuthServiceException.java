package com.example.jwt.common.exception;

public class OAuthServiceException extends RuntimeException {
    public OAuthServiceException(String message) {
        super(message);
    }
    public OAuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}