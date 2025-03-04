package com.boilerplate.boilerplate.config.jwt.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.boilerplate.boilerplate.config.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import java.time.Duration;
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
        Role testRole = Role.USER;
        User testUser = User.builder()
            .username(testUsername)
            .password(testPassword)
            .role(testRole)
            .build();
        userRepository.save(testUser);

        String token = jwtUtil.generateToken(testUser, Duration.ofMinutes(10));

        Long id = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        Role role = jwtUtil.getRole(token);

        assertThat(id).isEqualTo(testUser.getId());
        assertThat(username).isEqualTo(testUsername);
        assertThat(role).isEqualTo(testRole);
    }

    @Test
    @DisplayName("만료되지 않은 토큰 유효성 검사 통과")
    void generateToken_not_expired_success() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        User testUser = User.builder()
            .username(testUsername)
            .password(testPassword)
            .role(Role.USER)
            .build();
        userRepository.save(testUser);

        String token = jwtUtil.generateToken(testUser, Duration.ofDays(10));

        boolean result = jwtUtil.isValidToken(token);
        assertThat(result).isEqualTo(true);
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
        assertThat(result).isEqualTo(false);
    }

}