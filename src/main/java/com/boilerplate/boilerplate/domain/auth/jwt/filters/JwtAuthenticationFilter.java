package com.boilerplate.boilerplate.domain.auth.jwt.filters;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtTokenService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
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
    private final JwtConfig jwtConfig;
    private static final String TOKEN_REISSUE_URL = "/api/token/reissue";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        // 토큰 재발급 요청은 필터를 건너뛰도록 예외 처리
        // 헤더에서 Authorization: Bearer AccessToken 가져오기
        // Bearer AccessToken 에서 AccessToken 부분만 가져오기

        // 쿠키에서 Refresh Token 가져오기

        // Access Token이 유효한지 확인
        // 유효하다면, Token의 username으로부터 userDetails 가져오고 세션 생성하기

        // Access Token이 유효하지 않지만 Refresh Token이 유효하다면
        // 프론트에 토큰 재발급을 위해 "x-reissue-token" 헤더 응답
        // 프론트는 "x-reissue-token" 헤더가 오면 재발급 컨트롤러로 accessToken 재발급 요청 후
        //

        // Access Token이 유효하지 않을 때 냅다 재요청 보낸다면?

        String requestURI = request.getRequestURI();

        // /api/token/reissue 요청은 필터를 건너뛰도록 예외 처리
        if (requestURI.equals(TOKEN_REISSUE_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(jwtConfig.getHeaderAuthorization());
        String accessToken = getAccessToken(authorizationHeader);

        boolean isAccessTokenValid = accessToken != null && jwtTokenService.isValidToken(accessToken);

        if (isAccessTokenValid) {
            CustomUserDetails userDetails = jwtTokenService.getUserDetailsFromToken(accessToken);
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = CookieUtil.getCookieByName(request.getCookies(), jwtConfig.getRefreshTokenName());
        boolean isRefreshTokenValid = refreshToken != null && jwtTokenService.isValidToken(refreshToken);

        // AccessToken이 만료되었지만 RefreshToken이 유효한 경우 프론트에 재발급 요청 신호 보내기
        if (isRefreshTokenValid) {
            log.info("토큰 재발급 요청 쿠키 전송");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("x-reissue-token", "true");  // 프론트에서 감지해서 자동으로 재발급 요청
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(
            jwtConfig.getAccessTokenPrefix())) {
            return authorizationHeader.substring(jwtConfig.getAccessTokenPrefix().length());
        }
        return null;
    }
}
