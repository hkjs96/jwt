package com.example.jwt.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.jwt.common.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailDupException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailDup(EmailDupException ex) {
        return ResponseEntity
                .status(409)
                .body(ApiResponse.error("DUPLICATE_EMAIL", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex) {
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error("INTERNAL_ERROR", ex.getMessage()));
    }
}