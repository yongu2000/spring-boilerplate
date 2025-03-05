package com.boilerplate.boilerplate.config.jwt.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.boilerplate.boilerplate.config.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DisplayName("JWT Util")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testEmail", "testUser", "password", "testName", Role.USER);
        userRepository.save(user);

    }

    @Test
    @DisplayName("토큰 정상 발급")
    void generateToken_success() {
        //Given
        String token = jwtUtil.generateToken(user, Duration.ofMinutes(10));

        //When
        Long id = jwtUtil.getUserId(token);
        String email = jwtUtil.getEmail(token);
        String username = jwtUtil.getUsername(token);
        String name = jwtUtil.getName(token);
        Role role = jwtUtil.getRole(token);

        //Then
        assertThat(id).isEqualTo(user.getId());
        assertThat(username).isEqualTo(user.getUsername());
        assertThat(role).isEqualTo(user.getRole());
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