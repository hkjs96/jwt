package com.example.jwt.domain.auth.service;

import com.example.jwt.config.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis를 이용한 Refresh Token 저장소 서비스
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Refresh Token 저장 (기존에 저장된 값이 있으면 덮어쓰기)
     *
     * @param email        사용자 식별자 (email)
     * @param refreshToken 발급된 Refresh Token
     */
    public void save(String email, String refreshToken) {
        String key = buildKey(email);
        // JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_MS 밀리초만큼 TTL 설정
        redisTemplate.opsForValue()
                .set(key, refreshToken,
                        JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_MS,
                        TimeUnit.MILLISECONDS);
    }

    /**
     * 사용자별 Refresh Token 삭제 (로그아웃 시)
     */
    public void delete(String email) {
        redisTemplate.delete("refresh:" + email);
    }

    /**
     * Refresh Token 검증
     */
    public boolean validate(String email, String refreshToken) {
        String stored = redisTemplate.opsForValue().get("refresh:" + email);
        return refreshToken.equals(stored);
    }

    /** Redis에 저장할 때 사용하는 키 생성 */
    private String buildKey(String email) {
        return "refresh:" + email;
    }
}