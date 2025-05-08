package com.example.jwt.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Google 사용자 정보")
public record GoogleUserInfo(
        @Schema(description = "사용자 고유 ID", example = "10769150350006150715113082367") String sub,
        @Schema(description = "이메일 주소", example = "user@example.com") String email,
        @Schema(description = "이메일 검증 여부") boolean email_verified,
        @Schema(description = "이름", example = "홍길동") String name,
        @Schema(description = "프로필 사진 URL") String picture
) {}