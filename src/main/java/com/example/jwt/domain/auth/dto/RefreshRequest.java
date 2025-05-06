package com.example.jwt.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급 요청")
public record RefreshRequest(
        @Schema(example = "eyJhbGciOiJI...") @NotBlank String refreshToken
) {}