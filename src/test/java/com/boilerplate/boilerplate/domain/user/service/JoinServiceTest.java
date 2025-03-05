package com.boilerplate.boilerplate.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.boilerplate.boilerplate.domain.user.dto.JoinRequest;
import com.boilerplate.boilerplate.domain.user.dto.JoinResponse;
import com.boilerplate.boilerplate.domain.user.exception.UserError;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("회원가입 서비스 JoinService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@Transactional
class JoinServiceTest {

    @Autowired
    private JoinService joinService;
    @Autowired
    private UserRepository userRepository;

    String email = "testEmail";
    String username = "testUsername";
    String password = "testPassword";
    String name = "testName";

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void 회원가입_성공() {
        // Given
        JoinRequest request = new JoinRequest();
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(password);
        request.setName(name);

        // When
        JoinResponse response = joinService.join(request);

        // Then
        assertThat(response.getUsername()).isEqualTo(username);
    }

    @Test
    void 회원가입_실패_중복_username() {
        // Given
        JoinRequest request = new JoinRequest();
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(password);
        request.setName(name);
        
        JoinResponse first_response = joinService.join(request);

        // When & Then
        assertThatThrownBy(() -> joinService.join(request))
            .isInstanceOf(IllegalArgumentException.class).hasMessage(
                UserError.ALREADY_EXIST.getMessage());
    }
}