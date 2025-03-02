package com.boilerplate.boilerplate.domain.user.service;

import com.boilerplate.boilerplate.domain.user.dto.JoinRequest;
import com.boilerplate.boilerplate.domain.user.dto.JoinResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JoinServiceTest {

    @Autowired
    private JoinService joinService;

    @Test
    @DisplayName("회원가입 성공")
    void join_success() {
        JoinRequest request = new JoinRequest();
        request.setUsername("test");
        request.setPassword("test");
        JoinResponse response = joinService.join(request);
    }
}