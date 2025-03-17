package com.boilerplate.boilerplate.domain.auth.jwt.filters;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.service.AccessTokenService;
import com.boilerplate.boilerplate.domain.auth.jwt.service.RefreshTokenService;
import com.boilerplate.boilerplate.domain.user.dto.LoginRequest;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final JwtConfig jwtConfig;

    private static final String LOGIN_URL = "/api/login";

    public JwtLoginFilter(AuthenticationManager authenticationManager,
        AccessTokenService accessTokenService, RefreshTokenService refreshTokenService, JwtConfig jwtConfig) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.jwtConfig = jwtConfig;
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
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            return objectMapper.readValue(messageBody, LoginRequest.class);
        } catch (IOException e) {
            throw new JsonParseException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = accessTokenService.createAccessToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(userDetails);

        response.addHeader(jwtConfig.getHeaderAuthorization(),
            jwtConfig.getAccessTokenPrefix() + accessToken);
        CookieUtil.addCookie(response, jwtConfig.getRefreshTokenName(), refreshToken,
            (int) jwtConfig.getRefreshTokenExpiration().toSeconds());
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed) {
        throw failed;
    }

}
