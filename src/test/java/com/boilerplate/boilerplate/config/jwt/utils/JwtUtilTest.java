package com.boilerplate.boilerplate.config.jwt.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.boilerplate.boilerplate.config.JwtProperties;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import com.boilerplate.boilerplate.global.auth.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.global.utils.JwtUtil;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
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
    private JwtUserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("testEmail")
            .username("testUser")
            .password("password")
            .name("testName")
            .role(Role.USER)
            .build();
        userRepository.save(user);
        userDetails = new JwtUserDetails(user);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void 토큰_발급_성공() {
        // Given
        String token = jwtUtil.generateToken(userDetails, Duration.ofMinutes(10));

        // When
        Long id = jwtUtil.getUserId(token);
        String email = jwtUtil.getEmail(token);
        String username = jwtUtil.getUsername(token);
        String name = jwtUtil.getName(token);
        Role role = jwtUtil.getRole(token);

        // Then
        assertThat(id).isEqualTo(user.getId());
        assertThat(email).isEqualTo(user.getEmail());
        assertThat(username).isEqualTo(user.getUsername());
        assertThat(name).isEqualTo(user.getName());
        assertThat(role).isEqualTo(Role.of(user.getRole()));
    }

    @Test
    void 토큰_유효성_검사_성공() {
        // Given
        String token = jwtUtil.generateToken(userDetails, Duration.ofDays(10));
        // When
        boolean result = jwtUtil.isValidToken(token);
        // Then
        assertThat(result).isEqualTo(true);
    }

    @Test
    void 만료토큰_유효성_검사_성공() {
        // Given
        String token = jwtUtil.generateToken(userDetails, Duration.ofDays(-10));
        // When
        boolean result = jwtUtil.isValidToken(token);
        // Then
        assertThat(result).isEqualTo(false);
    }

}