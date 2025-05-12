package com.example.jwt.config.auth;

import com.example.jwt.domain.auth.dto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String ROLE_CLAIM = "role";
    private static final String REFRESH_ROLE = "REFRESH";
    public static final long REFRESH_TOKEN_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7;

    @Value("${jwt.secret}")
    private String secretKeyEncoded;
    @Value("${jwt.expiration}")
    private long expirationMinutes;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        log.info("JWT Provider initialized with expiration time: {} minutes", expirationMinutes);
    }

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyEncoded));
        }
        return secretKey;
    }

    public String createAccessToken(String email, String role) {
        long expirationMs = expirationMinutes * 60 * 1000;
        String token = createToken(email, role, expirationMs);

        Date expirationDate = new Date(System.currentTimeMillis() + expirationMs);
        LocalDateTime expirationDateTime = LocalDateTime.ofInstant(
                expirationDate.toInstant(), ZoneId.systemDefault());

        log.info("Access token created for user: {}, role: {}, expires at: {}",
                email, role, expirationDateTime);
        log.info("Created access token: {}", token);

        return token;
    }

    public String createRefreshToken(String email) {
        String token = createToken(email, REFRESH_ROLE, REFRESH_TOKEN_EXPIRATION_MS);

        Date expirationDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS);
        LocalDateTime expirationDateTime = LocalDateTime.ofInstant(
                expirationDate.toInstant(), ZoneId.systemDefault());

        log.info("Refresh token created for user: {}, expires at: {}",
                email, expirationDateTime);
        log.info("Created refresh token: {}", token);

        return token;
    }

    private String createToken(String email, String role, long expiry) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLE_CLAIM, role);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiry);

        return Jwts.builder()
                .subject(email)
                .claims(claims)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSecretKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((getSecretKey()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("❌ JWT token expired: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("❌ Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getClaims(token).get(ROLE_CLAIM, String.class);
    }

    public boolean isRefreshToken(String token) {
        return REFRESH_ROLE.equals(getRole(token));
    }

    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Access+Refresh 토큰 생성 및 응답 DTO 반환
     */
    public TokenResponse generateTokens(String email, String role) {
        String accessToken = createAccessToken(email, role);
        String refreshToken = createRefreshToken(email);
        long expiresIn = (getExpirationDate(accessToken).getTime() - System.currentTimeMillis()) / 1000;
        return new TokenResponse(accessToken, refreshToken, expiresIn);
    }
}