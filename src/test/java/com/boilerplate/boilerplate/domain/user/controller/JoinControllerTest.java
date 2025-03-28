package com.boilerplate.boilerplate.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.boilerplate.domain.user.dto.JoinRequest;
import com.boilerplate.boilerplate.domain.user.dto.JoinResponse;
import com.boilerplate.boilerplate.domain.user.exception.DuplicateUserException;
import com.boilerplate.boilerplate.domain.user.exception.UserError;
import com.boilerplate.boilerplate.domain.user.service.JoinService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(JoinController.class)
@DisplayName("유저 회원가입 Controller")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
class JoinControllerTest {

    @MockitoBean
    private JoinService joinService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long id = 1L;
    private String email = "email@email.com";
    private String username = "email";
    private String password = "password";
    private String name = "name";

    @Nested
    class 회원가입_성공 {

        @Test
        void 회원가입_성공_정상_회원가입() throws Exception {
            // given
            JoinRequest request = new JoinRequest(email, password);
            JoinResponse response = JoinResponse.builder()
                .id(id)
                .username(username)
                .build();
            given(joinService.join(any(JoinRequest.class))).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(print()) // 👈 응답 JSON 전체 콘솔 출력
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.username").value(username));
        }
    }

    @Nested
    class 회원가입_실패 {

        @Test
        void 회원가입_실패_중복된_이메일() throws Exception {
            // given
            JoinRequest request = new JoinRequest(email, password);

            given(joinService.join(any(JoinRequest.class)))
                .willThrow(new DuplicateUserException());

            // when & then
            mockMvc.perform(post("/api/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(UserError.DUPLICATE_USER.getMessage()))
                .andExpect(jsonPath("$.code").value(UserError.DUPLICATE_USER.name()))
                .andExpect(jsonPath("$.status").value(UserError.DUPLICATE_USER.getStatus().value()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.details").isMap());
        }
    }
}