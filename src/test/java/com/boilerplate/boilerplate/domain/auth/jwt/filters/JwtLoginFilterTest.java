//package com.boilerplate.boilerplate.domain.auth.jwt.filters;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
//import com.boilerplate.boilerplate.domain.auth.jwt.dto.LoginRequest;
//import com.boilerplate.boilerplate.global.config.JwtConfig;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.time.Duration;
//import java.util.Collections;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.DisplayNameGeneration;
//import org.junit.jupiter.api.DisplayNameGenerator;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("로그인 필터 테스트")
//@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//class JwtLoginFilterTest {
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//    @Mock
//    private AccessTokenService accessTokenService;
//    @Mock
//    private RefreshTokenService refreshTokenService;
//    @Mock
//    private JwtConfig jwtConfig;
//
//    private JwtLoginFilter jwtLoginFilter;
//    private MockHttpServletRequest request;
//    private MockHttpServletResponse response;
//
//    @BeforeEach
//    void setUp() {
//        jwtLoginFilter = new JwtLoginFilter(authenticationManager, accessTokenService,
//            refreshTokenService, jwtConfig);
//        request = new MockHttpServletRequest();
//        response = new MockHttpServletResponse();
//
//        // 공통 설정
//        request.setMethod("POST");
//        request.setContentType(MediaType.APPLICATION_JSON_VALUE);
//    }
//
//    @Test
//    void 로그인_성공_토큰_정상_발급() throws Exception {
//        // given
//        LoginRequest loginRequest = new LoginRequest("testUser", "password", false);
//        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);
//        request.setContent(requestBody.getBytes());
//
//        CustomUserDetails userDetails = mock(CustomUserDetails.class);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//            userDetails, null, Collections.emptyList());
//
//        when(authenticationManager.authenticate(any())).thenReturn(authentication);
//        when(accessTokenService.createAccessToken(userDetails)).thenReturn("access-token");
//        when(refreshTokenService.createRefreshToken(userDetails, Duration.ofDays(14))).thenReturn(
//            "refresh-token");
//        when(jwtConfig.getHeaderAuthorization()).thenReturn("Authorization");
//        when(jwtConfig.getRememberMeCookieName()).thenReturn("remember-me");
//        when(jwtConfig.getAccessTokenPrefix()).thenReturn("Bearer ");
//        when(jwtConfig.getRefreshTokenCookieName()).thenReturn("refresh-token");
//        when(jwtConfig.getRefreshTokenExpiration()).thenReturn(Duration.ofDays(14));
//
//        // when
//        Authentication result = jwtLoginFilter.attemptAuthentication(request, response);
//        jwtLoginFilter.successfulAuthentication(request, response, null, result);
//
//        // then
//        verify(accessTokenService).createAccessToken(userDetails);
//        verify(refreshTokenService).createRefreshToken(userDetails, Duration.ofDays(14));
//        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer access-token");
//    }
//
//    @Test
//    void 로그인_실패_잘못된_인증_정보() throws Exception {
//        // given
//        LoginRequest loginRequest = new LoginRequest("wrongUser", "wrongPassword", false);
//        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);
//        request.setContent(requestBody.getBytes());
//
//        // when
//        when(authenticationManager.authenticate(any()))
//            .thenThrow(new BadCredentialsException("Invalid credentials"));
//
//        // then
//        assertThrows(BadCredentialsException.class,
//            () -> jwtLoginFilter.attemptAuthentication(request, response));
//    }
//
/// /    @Test /    void 로그인_실패_잘못된_JSON_형식() { /        // given /
/// request.setContent("invalid json".getBytes()); / /        // when & then /
/// assertThrows(JsonParseException.class, /            () ->
/// jwtLoginFilter.attemptAuthentication(request, response)); /    }
//}