package com.boilerplate.boilerplate.domain.auth.jwt.filters;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.dto.LoginRequest;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.AuthenticationError;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.InvalidJsonRequestException;
import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtTokenService;
import com.boilerplate.boilerplate.global.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    private static final String LOGIN_URL = "/api/login";

    public JwtLoginFilter(AuthenticationManager authenticationManager,
        JwtTokenService jwtTokenService) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        setFilterProcessesUrl(LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = parseJsonLoginRequest(request);
            request.setAttribute("rememberMe", loginRequest.isRememberMe());

            String username = loginRequest.getUsername();
            username = username != null ? username.trim() : "";

            String password = loginRequest.getPassword();
            password = password != null ? password : "";

            UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken.unauthenticated(
                username, password);
            log.info("로그인 시도 중입니다 {}, {}", username, password);
            return authenticationManager.authenticate(authToken);
        } catch (InvalidJsonRequestException e) {
            throw new AuthenticationException(e.getMessage(), e) {
            };  // 익명 클래스로 예외 감싸기
        }
    }

    private LoginRequest parseJsonLoginRequest(HttpServletRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            return objectMapper.readValue(messageBody, LoginRequest.class);
        } catch (IOException e) {
            throw new InvalidJsonRequestException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        boolean rememberMe = (boolean) request.getAttribute("rememberMe");
        log.info("로그인 성공");

        String accessToken = jwtTokenService.createAccessToken(userDetails);
        String refreshToken = jwtTokenService.createRefreshToken(userDetails, rememberMe);

        jwtTokenService.setAccessToken(response, accessToken);
        jwtTokenService.setRefreshToken(response, refreshToken);

        log.info("토큰 발급 완료");
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed) throws IOException {

        ErrorResponse errorResponse;
        if (failed.getCause() instanceof InvalidJsonRequestException jsonException) {
            errorResponse = ErrorResponse.of(jsonException);
        } else {
            errorResponse = ErrorResponse.of(AuthenticationError.LOGIN_FAILED);
            errorResponse.addDetail("message", failed.getMessage());
        }
        response.setStatus(errorResponse.getStatus());
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writeValue(response.getWriter(), errorResponse);
    }

}
