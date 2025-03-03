package com.boilerplate.boilerplate.config.jwt;

import com.boilerplate.boilerplate.config.jwt.service.RefreshTokenService;
import com.boilerplate.boilerplate.config.jwt.utils.CookieUtil;
import com.boilerplate.boilerplate.config.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.dto.LoginRequest;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    private static final String LOGIN_URL = "/api/login";

    private static final Duration ACCESS_TOKEN_EXPIRATION_DURATION = Duration.ofMinutes(10);
    private static final Duration REFRESH_TOKEN_EXPIRATION_DURATION = Duration.ofDays(14);

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String REFRESH_TOKEN_NAME = "REFRESH_TOKEN";

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
        RefreshTokenService refreshTokenService) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        setFilterProcessesUrl(LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        LoginRequest loginRequest = parseJsonLoginRequest(request);

        String username = loginRequest.getUsername();
        username = username != null ? username.trim() : "";

        String password = loginRequest.getPassword();
        password = password != null ? password : "";

        UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken.unauthenticated(
            username, password);

        return authenticationManager.authenticate(authToken);
    }

    private LoginRequest parseJsonLoginRequest(HttpServletRequest request) {
        LoginRequest loginRequest;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginRequest = objectMapper.readValue(messageBody, LoginRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return loginRequest;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(user, ACCESS_TOKEN_EXPIRATION_DURATION);
        String refreshToken = jwtUtil.generateToken(user, REFRESH_TOKEN_EXPIRATION_DURATION);
        refreshTokenService.save(user, refreshToken);

        response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + accessToken);
        CookieUtil.addCookie(response, REFRESH_TOKEN_NAME, refreshToken,
            (int) REFRESH_TOKEN_EXPIRATION_DURATION.toSeconds());
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed) {
        log.info("failed");
    }

}
