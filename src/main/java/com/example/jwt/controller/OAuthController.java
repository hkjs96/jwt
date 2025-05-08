package com.example.jwt.controller;

import com.example.jwt.config.auth.JwtTokenProvider;
import com.example.jwt.domain.auth.service.GoogleOAuthService;
import com.example.jwt.domain.auth.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.jwt.common.ApiResponse;
import com.example.jwt.domain.auth.dto.AuthCodeRequest;
import com.example.jwt.domain.auth.dto.GoogleTokenResponse;
import com.example.jwt.domain.auth.dto.GoogleUserInfo;
import com.example.jwt.domain.user.entity.User;
import com.example.jwt.domain.user.entity.SocialType;
import com.example.jwt.domain.user.repository.UserRepository;
import com.example.jwt.domain.auth.dto.TokenResponse;

/**
 * Google OAuth 로그인 처리 컨트롤러
 */
@RestController
@RequestMapping("/oauth/google")
@RequiredArgsConstructor
public class OAuthController {

    private final GoogleOAuthService googleService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 구글 인가코드를 받아 JWT 로그인 처리
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody @Valid AuthCodeRequest request) {
        // 1) Google 토큰 요청
        GoogleTokenResponse tokenResp = googleService.requestToken(request.code());
        // 2) Google 프로필 조회
        GoogleUserInfo profile = googleService.requestUserInfo(tokenResp.accessToken());

        // 3) User 엔티티 신규/기존 분기 저장
        User user = userRepository
                .findBySocialTypeAndSocialId(SocialType.GOOGLE, profile.sub())
                .orElseGet(() -> userRepository.save(
                        User.ofSocial(profile.email(), SocialType.GOOGLE, profile.sub())
                ));

        // 4) JWT 토큰 생성 (Access + Refresh)
        TokenResponse jwtTokens = jwtProvider.generateTokens(user.getEmail(), user.getRoles().iterator().next());
        // 5) Refresh Token Redis 저장
        refreshTokenService.save(user.getEmail(), jwtTokens.refreshToken());

        // 6) 클라이언트로 JWT 전달
        return ResponseEntity.ok(ApiResponse.success(jwtTokens));
    }
}