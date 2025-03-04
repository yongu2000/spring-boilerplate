package com.boilerplate.boilerplate.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.exception.UserError;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void before() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("유저 검색에 성공")
    void findUser_success() {
        String testUsername = "testUsername";
        String testPassword = "testPassword";
        Role testRole = Role.USER;

        User testUser = User.builder()
            .username(testUsername)
            .password(testPassword)
            .role(testRole)
            .build();
        userRepository.save(testUser);

        Long testUserId = testUser.getId();
        User user = userService.findById(testUserId);

        assertThat(user.getUsername()).isEqualTo(testUsername);
        assertThat(user.getId()).isEqualTo(testUserId);
    }

    @Test
    @DisplayName("유저 검색에 실패: 유저가 존재하지 않음")
    void findUser_failure_no_user() {
        Long testUserId = 1L;

        assertThatThrownBy(() -> userService.findById(testUserId))
            .isInstanceOf(EntityNotFoundException.class).hasMessage(
                UserError.NO_SUCH_USER.getMessage());
    }
}