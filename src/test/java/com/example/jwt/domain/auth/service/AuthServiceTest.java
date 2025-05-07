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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "password123");
        given(userRepository.existsByEmail(anyString())).willReturn(false);

        // when
        authService.signup(request);

        // then
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_fail_email_duplicate() {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "password123");
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(EmailDupException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");

        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded_password")
                .roles(Set.of("ROLE_USER"))
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user), null, null);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(jwtTokenProvider.createAccessToken(anyString(), anyString()))
                .willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(anyString()))
                .willReturn("refresh-token");
        given(jwtTokenProvider.getExpirationDate(anyString()))
                .willReturn(new Date(System.currentTimeMillis() + 3600000));
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        TokenResponse response = authService.login(request);

        // then
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.expiresIn()).isPositive();

        then(valueOperations).should().set(
                eq("refresh:test@example.com"),
                eq("refresh-token"),
                eq(JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_MS),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    void refresh_success() {
        // given
        RefreshRequest request = new RefreshRequest("refresh-token");

        given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
        given(jwtTokenProvider.isRefreshToken(anyString())).willReturn(true);
        given(jwtTokenProvider.getSubject(anyString())).willReturn("test@example.com");
        given(jwtTokenProvider.getRole(anyString())).willReturn("ROLE_USER");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("refresh-token");
        given(jwtTokenProvider.createAccessToken(anyString(), anyString()))
                .willReturn("new-access-token");
        given(jwtTokenProvider.createRefreshToken(anyString()))
                .willReturn("new-refresh-token");
        given(jwtTokenProvider.getExpirationDate(anyString()))
                .willReturn(new Date(System.currentTimeMillis() + 3600000));

        // when
        TokenResponse response = authService.refresh(request);

        // then
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(response.expiresIn()).isPositive();

        then(valueOperations).should().set(
                eq("refresh:test@example.com"),
                eq("new-refresh-token"),
                eq(JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_MS),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 리프레시 토큰")
    void refresh_fail_invalid_token() {
        // given
        RefreshRequest request = new RefreshRequest("invalid-refresh-token");
        given(jwtTokenProvider.validateToken(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(JwtException.class)
                .hasMessage("Invalid Refresh Token");
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 리프레시 토큰이 아님")
    void refresh_fail_not_refresh_token() {
        // given
        RefreshRequest request = new RefreshRequest("access-token");
        given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
        given(jwtTokenProvider.isRefreshToken(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(JwtException.class)
                .hasMessage("Invalid Refresh Token");
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 저장된 토큰과 불일치")
    void refresh_fail_token_mismatch() {
        // given
        RefreshRequest request = new RefreshRequest("refresh-token");

        given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
        given(jwtTokenProvider.isRefreshToken(anyString())).willReturn(true);
        given(jwtTokenProvider.getSubject(anyString())).willReturn("test@example.com");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("different-refresh-token");

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(JwtException.class)
                .hasMessage("Refresh Token Mismatch");
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_success() {
        // given
        String email = "test@example.com";
        given(redisTemplate.delete(anyString())).willReturn(true);

        // when
        authService.logout(email);

        // then
        then(redisTemplate).should().delete(eq("refresh:test@example.com"));
    }
}