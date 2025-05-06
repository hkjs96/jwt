package com.example.jwt.domain.auth.service;

import com.example.jwt.common.exception.EmailDupException;
import com.example.jwt.config.CustomUserDetails;
import com.example.jwt.config.auth.JwtTokenProvider;
import com.example.jwt.domain.auth.dto.LoginRequest;
import com.example.jwt.domain.auth.dto.RefreshRequest;
import com.example.jwt.domain.auth.dto.SignupRequest;
import com.example.jwt.domain.auth.dto.TokenResponse;
import com.example.jwt.domain.user.entity.User;
import com.example.jwt.domain.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email()))
            throw new EmailDupException("이미 사용 중인 이메일입니다.");
        User user = User.builder()
                .email(req.email())
                .password(req.password()) // TODO: PasswordEncoder 적용 필요
                .roles(Set.of("ROLE_USER"))
                .build();
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );
        CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
        User user = customUser.getUser(); // 👍 안전하게 접근

        String at = jwtProvider.createAccessToken(user.getEmail(), user.getRoles().iterator().next());
        String rt = jwtProvider.createRefreshToken(user.getEmail());
        redisTemplate.opsForValue()
                .set("refresh:" + user.getEmail(), rt,
                        JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_MS, TimeUnit.MILLISECONDS);
        long expiresIn = jwtProvider.getExpirationDate(at).getTime() - System.currentTimeMillis();
        return new TokenResponse(at, rt, expiresIn / 1000);
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(RefreshRequest req) {
        String refresh = req.refreshToken();
        if (!jwtProvider.validateToken(refresh) || !jwtProvider.isRefreshToken(refresh))
            throw new JwtException("Invalid Refresh Token");
        String email = jwtProvider.getSubject(refresh);
        String stored = redisTemplate.opsForValue().get("refresh:" + email);
        if (!refresh.equals(stored))
            throw new JwtException("Refresh Token Mismatch");
        String at = jwtProvider.createAccessToken(email, jwtProvider.getRole(refresh));
        String rt = jwtProvider.createRefreshToken(email);
        redisTemplate.opsForValue()
                .set("refresh:" + email, rt,
                        JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_MS, TimeUnit.MILLISECONDS);
        long expiresIn = jwtProvider.getExpirationDate(at).getTime() - System.currentTimeMillis();
        return new TokenResponse(at, rt, expiresIn / 1000);
    }

    public void logout(String email) {
        redisTemplate.delete("refresh:" + email);
    }
}