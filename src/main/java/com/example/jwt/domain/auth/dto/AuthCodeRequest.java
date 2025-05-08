package com.example.jwt.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "OAuth 인가 코드 요청")
public record AuthCodeRequest(
        @NotBlank(message = "Authorization code must not be blank")
        @Schema(description = "Authorization Code", example = "4/0AY0e-g6..." )
        String code
) {}