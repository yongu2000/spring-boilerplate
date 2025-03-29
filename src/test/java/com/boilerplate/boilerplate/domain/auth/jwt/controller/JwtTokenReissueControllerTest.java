package com.boilerplate.boilerplate.domain.auth.jwt.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.boilerplate.domain.auth.jwt.exception.InvalidRefreshTokenException;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.RefreshTokenNotFoundException;
import com.boilerplate.boilerplate.domain.auth.jwt.service.AccessTokenService;
import com.boilerplate.boilerplate.domain.auth.jwt.service.RefreshTokenService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(JwtTokenReissueController.class)
@Import(JwtTokenReissueControllerTest.TestSecurityConfig.class)
@DisplayName("토큰 재발급 Controller")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtTokenReissueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private JwtConfig jwtConfig;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
    private static final String REMEMBER_ME_COOKIE_NAME = "REMEMBER_ME";
    private static final String VALID_REFRESH_TOKEN = "VALID_REFRESH_TOKEN";
    private static final String NEW_ACCESS_TOKEN = "NEW_ACCESS_TOKEN";
    private static final String NEW_REFRESH_TOKEN = "NEW_REFRESH_TOKEN";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    static class TestSecurityConfig {

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
    void 유효한_리프레시_토큰_새로운_Access_Token_Refresh_Token_재발급_기본_만료시간() throws Exception {
        // given
        given(accessTokenService.reissueAccessToken(VALID_REFRESH_TOKEN)).willReturn(NEW_ACCESS_TOKEN);
        given(refreshTokenService.reissueRefreshToken(VALID_REFRESH_TOKEN, jwtConfig.getRefreshTokenExpiration()))
            .willReturn(NEW_REFRESH_TOKEN);

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, VALID_REFRESH_TOKEN);
        Cookie rememberMeCookie = new Cookie(REMEMBER_ME_COOKIE_NAME, "false"); // 🔥 rememberMe = false

        // when & then
        mockMvc.perform(post("/api/token/reissue")
                .cookie(refreshTokenCookie)
                .cookie(rememberMeCookie)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated())
            .andExpect(header().string(AUTHORIZATION_HEADER, TOKEN_PREFIX + NEW_ACCESS_TOKEN))
            .andExpect(cookie().value(REFRESH_TOKEN_COOKIE_NAME, NEW_REFRESH_TOKEN));

        verify(accessTokenService).reissueAccessToken(VALID_REFRESH_TOKEN);
        verify(refreshTokenService).reissueRefreshToken(VALID_REFRESH_TOKEN, jwtConfig.getRefreshTokenExpiration());
    }

    @Test
    void 유효한_리프레시_토큰_새로운_Access_Token_Refresh_Token_재발급_긴_만료시간() throws Exception {
        // given
        given(accessTokenService.reissueAccessToken(VALID_REFRESH_TOKEN)).willReturn(NEW_ACCESS_TOKEN);
        given(refreshTokenService.reissueRefreshToken(VALID_REFRESH_TOKEN,
            jwtConfig.getRememberMeRefreshTokenExpiration()))
            .willReturn(NEW_REFRESH_TOKEN);

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, VALID_REFRESH_TOKEN);
        Cookie rememberMeCookie = new Cookie(REMEMBER_ME_COOKIE_NAME, "true"); // 🔥 rememberMe = true

        // when & then
        mockMvc.perform(post("/api/token/reissue")
                .cookie(refreshTokenCookie)
                .cookie(rememberMeCookie)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated())
            .andExpect(header().string(AUTHORIZATION_HEADER, TOKEN_PREFIX + NEW_ACCESS_TOKEN))
            .andExpect(cookie().value(REFRESH_TOKEN_COOKIE_NAME, NEW_REFRESH_TOKEN));

        verify(accessTokenService).reissueAccessToken(VALID_REFRESH_TOKEN);
        verify(refreshTokenService).reissueRefreshToken(VALID_REFRESH_TOKEN,
            jwtConfig.getRememberMeRefreshTokenExpiration());
    }

    @Test
    @DisplayName("리프레시 토큰이 없는 경우 400 에러를 반환한다")
    void 리프레시_토큰_없음_BAD_REQUEST() throws Exception {
        // given
        given(accessTokenService.reissueAccessToken(null))
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

        given(accessTokenService.reissueAccessToken(invalidToken))
            .willThrow(new InvalidRefreshTokenException());

        // when & then
        mockMvc.perform(post("/api/token/reissue")
                .cookie(refreshTokenCookie)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
} 