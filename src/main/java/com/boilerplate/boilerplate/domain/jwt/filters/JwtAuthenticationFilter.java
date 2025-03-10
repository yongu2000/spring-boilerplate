package com.boilerplate.boilerplate.domain.jwt.filters;

import com.boilerplate.boilerplate.domain.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.domain.jwt.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private static final String TOKEN_REISSUE_URL = "/api/token/header";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // /api/token/header 요청은 필터를 건너뛰도록 예외 처리
        if (requestURI.equals(TOKEN_REISSUE_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(JwtProperties.HEADER_AUTHORIZATION);
        String accessToken = getAccessToken(authorizationHeader);
        String refreshToken = jwtTokenService.getRefreshTokenFromCookie(request.getCookies());

        boolean isAccessTokenValid = accessToken != null && jwtTokenService.isValidToken(accessToken);
        boolean isRefreshTokenValid = refreshToken != null && jwtTokenService.isValidToken(refreshToken);

        if (isAccessTokenValid) {
            JwtUserDetails userDetails = jwtTokenService.getUserDetailsFromToken(accessToken);
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // AccessToken이 만료되었지만 RefreshToken이 유효한 경우 프론트에 재발급 요청 신호 보내기
        if (!isAccessTokenValid && isRefreshTokenValid) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("x-reissue-token", "true");  // 프론트에서 감지해서 자동으로 재발급 요청
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(
            JwtProperties.ACCESS_TOKEN_PREFIX)) {
            return authorizationHeader.substring(JwtProperties.ACCESS_TOKEN_PREFIX.length());
        }
        return null;
    }
}
