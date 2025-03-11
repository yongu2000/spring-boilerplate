package com.boilerplate.boilerplate.domain.auth.jwt.filters;

import com.boilerplate.boilerplate.domain.auth.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtTokenService;
import com.boilerplate.boilerplate.domain.auth.jwt.service.RefreshTokenService;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class JwtLogoutFilter extends GenericFilterBean {

    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    private static final String LOGOUT_URL = "^/api/logout$";
    private static final String METHOD_POST = "POST";


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse,
            filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        if (!requestUri.matches(LOGOUT_URL)) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals(METHOD_POST)) {

            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = CookieUtil.getCookieByName(request.getCookies(),
            JwtProperties.REFRESH_TOKEN_NAME);

        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!jwtTokenService.isValidToken(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        //DB에 저장되어 있는지 확인
        try {
            refreshTokenService.findByRefreshToken(refreshToken);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        refreshTokenService.deleteByRefreshToken(refreshToken);

        CookieUtil.deleteCookie(request, response, JwtProperties.REFRESH_TOKEN_NAME);
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
