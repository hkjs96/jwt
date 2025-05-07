package com.example.jwt.controller;

import com.example.jwt.common.exception.EmailDupException;
import com.example.jwt.config.CustomUserDetails;
import com.example.jwt.config.CustomUserDetailsService;
import com.example.jwt.config.JwtAuthenticationFilter;
import com.example.jwt.config.SecurityConfig;
import com.example.jwt.config.auth.JwtTokenProvider;
import com.example.jwt.domain.auth.dto.LoginRequest;
import com.example.jwt.domain.auth.dto.RefreshRequest;
import com.example.jwt.domain.auth.dto.SignupRequest;
import com.example.jwt.domain.auth.dto.TokenResponse;
import com.example.jwt.domain.auth.service.AuthService;
import com.example.jwt.domain.user.entity.User;
import com.example.jwt.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_success() throws Exception {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "password123");
        willDoNothing().given(authService).signup(any(SignupRequest.class));

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_fail_email_duplicate() throws Exception {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "password123");
        willThrow(new EmailDupException("이미 사용 중인 이메일입니다."))
                .given(authService).signup(any(SignupRequest.class));

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_success() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        TokenResponse response = new TokenResponse("access-token", "refresh-token", 3600);
        given(authService.login(any(LoginRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600));
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    void refresh_success() throws Exception {
        // given
        RefreshRequest request = new RefreshRequest("refresh-token");
        TokenResponse response = new TokenResponse("new-access-token", "new-refresh-token", 3600);
        given(authService.refresh(any(RefreshRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600));
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 리프레시 토큰")
    void refresh_fail_invalid_token() throws Exception {
        // given
        RefreshRequest request = new RefreshRequest("invalid-refresh-token");
        willThrow(new JwtException("Invalid Refresh Token"))
                .given(authService).refresh(any(RefreshRequest.class));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Invalid Refresh Token"));
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_success() throws Exception {
        // given
        String email = "test@example.com";
        willDoNothing().given(authService).logout(anyString());

        // when & then
        mockMvc.perform(post("/api/auth/logout")
                        .param("email", email)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 성공 테스트")
    void me_success() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded_password")
                .roles(Set.of("ROLE_USER"))
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.roles[0]").value("ROLE_USER"));
    }
}