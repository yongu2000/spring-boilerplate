package com.boilerplate.boilerplate.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.exception.UserError;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("회원 서비스 UserService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    private User user;

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
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void 유저_조회_성공() {
        // Given
        Long userId = user.getId();

        // When
        User testUser = userService.findById(userId);

        // Then
        assertThat(testUser.getId()).isEqualTo(user.getId());
        assertThat(testUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(testUser.getName()).isEqualTo(user.getName());
        assertThat(testUser.getRole()).isEqualTo(user.getRole());
    }

    @Test
    void 유저_조회_실패_유저없음() {
        // Given
        Long testUserId = 99L;

        // When & Then
        assertThatThrownBy(() -> userService.findById(testUserId))
            .isInstanceOf(EntityNotFoundException.class).hasMessage(
                UserError.USER_NOT_FOUND.getMessage());
    }
}