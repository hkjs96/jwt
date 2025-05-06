package com.example.jwt.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "회원가입 요청")
public record SignupRequest(
        @Schema(example = "user@example.com") @Email @NotBlank String email,
        @Schema(example = "P@ssw0rd!") @NotBlank @Size(min = 8, max = 20) String password
) {}