package com.example.jwt.config.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private final String testEmail = "test@example.com";
    private final String testRole = "ROLE_USER";
    private final String testSecret = Base64.getEncoder().encodeToString(
            "test-secret-key-must-be-longer-than-256-bits-for-hmac-sha-algorithm".getBytes());
    private final long testExpiration = 10; // 10분

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKeyEncoded", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "expirationMinutes", testExpiration);
        jwtTokenProvider.init(); // Call @PostConstruct method
    }

    @Test
    @DisplayName("액세스 토큰 생성 테스트")
    void createAccessToken() {
        // when
        String token = jwtTokenProvider.createAccessToken(testEmail, testRole);

        // then
        assertThat(token).isNotEmpty();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getSubject(token)).isEqualTo(testEmail);
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo(testRole);
        assertThat(jwtTokenProvider.isRefreshToken(token)).isFalse();
    }

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    void createRefreshToken() {
        // when
        String token = jwtTokenProvider.createRefreshToken(testEmail);

        // then
        assertThat(token).isNotEmpty();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getSubject(token)).isEqualTo(testEmail);
        assertThat(jwtTokenProvider.isRefreshToken(token)).isTrue();
    }

    @Test
    @DisplayName("토큰 유효성 검사 성공 테스트")
    void validateToken_success() {
        // given
        String token = jwtTokenProvider.createAccessToken(testEmail, testRole);

        // when & then
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 - 만료된 토큰")
    void validateToken_fail_expired() throws Exception {
        // given
        // 테스트용 임시 JwtTokenProvider 생성
        JwtTokenProvider testProvider = new JwtTokenProvider();
        // 토큰의 만료 시간을 과거로 설정
        ReflectionTestUtils.setField(testProvider, "secretKeyEncoded", testSecret);
        ReflectionTestUtils.setField(testProvider, "expirationMinutes", -10); // -10분으로 설정 (과거)

        // init 메서드 호출
        Method initMethod = JwtTokenProvider.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(testProvider);

        // 만료된 토큰 생성
        String expiredToken = testProvider.createAccessToken(testEmail, testRole);

        // when & then
        assertThat(jwtTokenProvider.validateToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("토큰 유효성 검사 실패 - 잘못된 형식의 토큰")
    void validateToken_fail_invalid_format() {
        // given
        String invalidToken = "invalid.jwt.token";

        // when & then
        assertThat(jwtTokenProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("토큰에서 subject 추출 테스트")
    void getSubject() {
        // given
        String token = jwtTokenProvider.createAccessToken(testEmail, testRole);

        // when
        String subject = jwtTokenProvider.getSubject(token);

        // then
        assertThat(subject).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("토큰에서 role 추출 테스트")
    void getRole() {
        // given
        String token = jwtTokenProvider.createAccessToken(testEmail, testRole);

        // when
        String role = jwtTokenProvider.getRole(token);

        // then
        assertThat(role).isEqualTo(testRole);
    }

    @Test
    @DisplayName("리프레시 토큰 확인 테스트 - 리프레시 토큰")
    void isRefreshToken_true() {
        // given
        String refreshToken = jwtTokenProvider.createRefreshToken(testEmail);

        // when & then
        assertThat(jwtTokenProvider.isRefreshToken(refreshToken)).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰 확인 테스트 - 액세스 토큰")
    void isRefreshToken_false() {
        // given
        String accessToken = jwtTokenProvider.createAccessToken(testEmail, testRole);

        // when & then
        assertThat(jwtTokenProvider.isRefreshToken(accessToken)).isFalse();
    }

    @Test
    @DisplayName("토큰 만료일 추출 테스트")
    void getExpirationDate() {
        // given
        String token = jwtTokenProvider.createAccessToken(testEmail, testRole);

        // when
        Date expirationDate = jwtTokenProvider.getExpirationDate(token);

        // then
        assertThat(expirationDate).isAfter(new Date());
        long expectedExpirationTime = new Date().getTime() + (testExpiration * 60 * 1000);
        // 1초 오차 허용
        assertThat(expirationDate.getTime()).isBetween(
                expectedExpirationTime - 1000,
                expectedExpirationTime + 1000
        );
    }

    @Test
    @DisplayName("getClaims 메서드 테스트")
    void getClaims() throws Exception {
        // given
        String token = jwtTokenProvider.createAccessToken(testEmail, testRole);

        // when
        Method getClaimsMethod = JwtTokenProvider.class.getDeclaredMethod("getClaims", String.class);
        getClaimsMethod.setAccessible(true);
        Claims claims = (Claims) getClaimsMethod.invoke(jwtTokenProvider, token);

        // then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(testEmail);
        assertThat(claims.get("role")).isEqualTo(testRole);
    }

    // getSecretKey 메서드 테스트 추가
    @Test
    @DisplayName("getSecretKey 메서드 테스트")
    void getSecretKey() throws Exception {
        // when
        Method getSecretKeyMethod = JwtTokenProvider.class.getDeclaredMethod("getSecretKey");
        getSecretKeyMethod.setAccessible(true);
        Object secretKey = getSecretKeyMethod.invoke(jwtTokenProvider);

        // then
        assertThat(secretKey).isNotNull();
    }

    @Test
    @DisplayName("토큰 처리 예외 발생 테스트")
    void handle_exception_when_processing_invalid_token() {
        // given
        String invalidToken = "invalid.token";

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.getSubject(invalidToken))
                .isInstanceOf(JwtException.class);
    }
}