package com.boilerplate.boilerplate.domain.user.service;

import com.boilerplate.boilerplate.domain.user.dto.JoinRequest;
import com.boilerplate.boilerplate.domain.user.dto.JoinResponse;
import com.boilerplate.boilerplate.domain.user.exception.UserError;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JoinServiceTest {

    @Autowired
    private JoinService joinService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void before() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void join_success() {
        JoinRequest request = new JoinRequest();
        String username = "testID";
        String password = "testPW";
        request.setUsername(username);
        request.setPassword(password);
        JoinResponse response = joinService.join(request);
        Assertions.assertThat(response.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("회원가입 실패: 중복 username")
    void join_fail_existing_username() {
        JoinRequest request = new JoinRequest();
        String username = "testID";
        String password = "testPW";
        request.setUsername(username);
        request.setPassword(password);

        JoinResponse first_response = joinService.join(request);

        Assertions.assertThatThrownBy(() -> joinService.join(request))
            .isInstanceOf(IllegalArgumentException.class).hasMessage(
                UserError.ALREADY_EXIST.getMessage());
    }
}