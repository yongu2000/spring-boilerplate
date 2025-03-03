package com.boilerplate.boilerplate.config.jwt;

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

    private static final String LOGIN_URL = "/api/login";
    private static final Duration EXPIRATION_DURATION = Duration.ofMinutes(10);
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
        log.info("username = {}", username);
        log.info("password = {}", password);
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
        String accessToken = jwtUtil.generateToken(user, EXPIRATION_DURATION);
        response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + accessToken);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed) {
        log.info("failed");
    }

}
