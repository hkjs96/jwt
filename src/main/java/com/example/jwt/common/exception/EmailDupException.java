package com.example.jwt.common.exception;

public class EmailDupException extends RuntimeException {
    public EmailDupException(String message) {
        super(message);
    }
}