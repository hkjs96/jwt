package com.example.jwt.domain.user.repository;

import com.example.jwt.domain.user.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jwt.domain.user.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}