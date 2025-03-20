package com.boilerplate.boilerplate.domain.auth.jwt.controller;

import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtLogoutService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logout")
@RequiredArgsConstructor
public class JwtLogoutController {

    private final JwtLogoutService jwtLogoutService;
    private final JwtConfig jwtConfig;

    @PostMapping()
    public ResponseEntity<?> logout(HttpServletRequest request,
        HttpServletResponse response) {

        String refreshToken = CookieUtil.getCookieByName(request.getCookies(), jwtConfig.getRefreshTokenCookieName());

        jwtLogoutService.logout(refreshToken);

        CookieUtil.deleteCookie(request, response, jwtConfig.getRememberMeCookieName());
        CookieUtil.deleteCookie(request, response, jwtConfig.getRefreshTokenCookieName());
        return ResponseEntity.ok().build();
    }

}
