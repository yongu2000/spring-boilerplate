package com.boilerplate.boilerplate.config.jwt.utils;

import com.boilerplate.boilerplate.config.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void before() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("토큰 정상 발급")
    void generateToken_success() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        User testUser = User.builder()
            .username(testUsername)
            .password(testPassword)
            .role(Role.USER)
            .build();
        userRepository.save(testUser);

        String token = jwtUtil.generateToken(testUser, Duration.ofMinutes(10));

        String username = jwtUtil.getUsername(token);
        Assertions.assertThat(username).isEqualTo(testUsername);
    }

    @Test
    @DisplayName("만료된 토큰 유효성 검증 실패")
    void generateToken_expired_failure() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        User testUser = User.builder()
            .username(testUsername)
            .password(testPassword)
            .role(Role.USER)
            .build();
        userRepository.save(testUser);

        String token = jwtUtil.generateToken(testUser, Duration.ofDays(-10));

        boolean result = jwtUtil.isValidToken(token);
        Assertions.assertThat(result).isEqualTo(false);
    }

}