package com.boilerplate.boilerplate.domain.email.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.boilerplate.domain.email.dto.VerifyCodeResponse;
import com.boilerplate.boilerplate.domain.email.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmailController.class)
@DisplayName("이메일 Controller")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @Test
    void 인증코드_이메일_전송_성공() throws Exception {
        mockMvc.perform(post("/api/email/send/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void 비밀번호_초기화_이메일_전송_성공() throws Exception {
        mockMvc.perform(post("/api/email/send/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void 인증코드_검증_성공() throws Exception {
        given(emailService.verifyCode(anyString(), anyString()))
            .willReturn(new VerifyCodeResponse(true));

        mockMvc.perform(post("/api/email/verify/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\", \"code\":\"123456\"}"))
            .andExpect(status().isOk());
    }
}
