package com.example.jwt.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "로그인 요청")
public record LoginRequest(
        @Schema(example = "user@example.com") @Email @NotBlank String email,
        @Schema(example = "P@ssw0rd!") @NotBlank String password
) {}