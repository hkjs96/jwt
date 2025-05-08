package com.example.jwt.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // 소셜 로그인 유저는 password 가 없으므로 nullable=true 로 변경
    @Column(nullable = true)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    @Enumerated(EnumType.STRING)
    SocialType socialType;     // GOOGLE, NAVER, APPLE 등
    String socialId;

    // 소셜 로그인 시 호출될 팩토리 메서드
    public static User ofSocial(String email, SocialType type, String socialId) {
        return User.builder()
                .email(email)
                // 소셜 로그인 유저용 더미 비밀번호 (DB 제약 회피)
                .password(UUID.randomUUID().toString())
                .roles(Set.of("ROLE_USER"))
                .socialType(type)
                .socialId(socialId)
                .build();
    }
}