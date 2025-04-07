package com.boilerplate.boilerplate.domain.auth.jwt.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.boilerplate.domain.auth.jwt.exception.InvalidRefreshTokenException;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.RefreshTokenNotFoundException;
import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtTokenService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@WebMvcTest(JwtTokenReissueController.class)
@Import(JwtTokenReissueControllerTest.TestSecurityConfig.class)
@DisplayName("토큰 재발급 Controller")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtTokenReissueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @MockitoBean
    private JwtConfig jwtConfig;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
    private static final String VALID_REFRESH_TOKEN = "VALID_REFRESH_TOKEN";
    private static final String NEW_ACCESS_TOKEN = "NEW_ACCESS_TOKEN";
    private static final String NEW_REFRESH_TOKEN = "NEW_REFRESH_TOKEN";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    static class TestSecurityConfig { // 테스트 전용 Security 환경

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setUp() {
        given(jwtConfig.getRefreshTokenCookieName()).willReturn(REFRESH_TOKEN_COOKIE_NAME);
        given(jwtConfig.getHeaderAuthorization()).willReturn(AUTHORIZATION_HEADER);
        given(jwtConfig.getAccessTokenPrefix()).willReturn(TOKEN_PREFIX);
    }

    @Test
    void 유효한_리프레시_토큰_새로운_Access_Token_Refresh_Token_재발급() throws Exception {
        // given
        given(jwtTokenService.reissueAccessToken(VALID_REFRESH_TOKEN)).willReturn(
            NEW_ACCESS_TOKEN);
        given(jwtTokenService.reissueRefreshToken(VALID_REFRESH_TOKEN)).willReturn(
            NEW_REFRESH_TOKEN);

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, VALID_REFRESH_TOKEN);

        // when & then
        mockMvc.perform(post("/api/token/reissue")
                .cookie(refreshTokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated());
//            .andDo(document("token-reissue",
//                resource(
//                    ResourceSnippetParameters.builder()
//                        .tag("인증")
//                        .summary("토큰 재발급 API")
//                        .description(
//                            "유효한 Refresh Token을 통해 새로운 Access Token 및 Refresh Token을 발급받습니다.")
//                        .requestFields(
//                            fieldWithPath("REFRESH_TOKEN").type(JsonFieldType.STRING)
//                                .description("기존 리프레쉬 토큰 - 쿠키로 전송")
//                        )
//                        .responseFields(
//                            fieldWithPath("Authorization").type(JsonFieldType.STRING)
//                                .description("새로운 액세스 토큰 (Bearer 포함) - 헤더로 발급"),
//                            fieldWithPath("REFRESH_TOKEN").type(JsonFieldType.STRING)
//                                .description("새로운 리프레쉬 토큰 - 쿠키로 발급")
//                        )
//                        .requestSchema(schema("TokenReissueRequest"))
//                        .responseSchema(schema("TokenReissueResponse"))
//                        .build()
//                )
//            ));

        verify(jwtTokenService).setAccessToken(any(HttpServletResponse.class),
            eq(NEW_ACCESS_TOKEN));
        verify(jwtTokenService).setRefreshToken(any(HttpServletResponse.class),
            eq(NEW_REFRESH_TOKEN));

    }

    @Test
    @DisplayName("리프레시 토큰이 없는 경우 400 에러를 반환한다")
    void 리프레시_토큰_없음_BAD_REQUEST() throws Exception {
        // given
        given(jwtTokenService.reissueAccessToken(null))
            .willThrow(new RefreshTokenNotFoundException());

        // when & then
        mockMvc.perform(post("/api/token/reissue")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 요청시 401 에러를 반환한다")
    void 유효하지_않은_리프레시_토큰_UNAUTHORIZED() throws Exception {
        // given
        String invalidToken = "invalid.refresh.token";
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, invalidToken);

        given(jwtTokenService.reissueAccessToken(invalidToken))
            .willThrow(new InvalidRefreshTokenException());

        // when & then
        mockMvc.perform(post("/api/token/reissue")
                .cookie(refreshTokenCookie)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}