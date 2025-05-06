package com.example.jwt.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 응답")
public record TokenResponse(
        @Schema(example = "eyJhbGci...") String accessToken,
        @Schema(example = "eyJhbGci...") String refreshToken,
        @Schema(description = "Access Token 만료 시간(초)", example = "3600") long expiresIn
) {}