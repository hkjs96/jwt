package com.example.jwt.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Google OAuth2 토큰 응답")
public record GoogleTokenResponse(
        @Schema(description = "Access Token")
        @JsonProperty("access_token") String accessToken,
        @Schema(description = "Refresh Token")
        @JsonProperty("refresh_token") String refreshToken,
        @Schema(description = "만료 시간(초)")
        @JsonProperty("expires_in") int expiresIn,
        @Schema(description = "토큰 타입", example = "Bearer")
        @JsonProperty("token_type") String tokenType
) {}