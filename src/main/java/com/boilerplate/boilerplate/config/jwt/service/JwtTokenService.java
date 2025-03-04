package com.boilerplate.boilerplate.config.jwt.service;

import com.boilerplate.boilerplate.config.jwt.JwtProperties;
import com.boilerplate.boilerplate.config.jwt.entity.RefreshToken;
import com.boilerplate.boilerplate.config.jwt.exception.TokenError;
import com.boilerplate.boilerplate.config.jwt.utils.CookieUtil;
import com.boilerplate.boilerplate.config.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String getRefreshTokenFromCookie(Cookie[] cookies) {
        return CookieUtil.getCookieByName(cookies, JwtProperties.REFRESH_TOKEN_NAME);
    }

    public String createNewAccessToken(String refreshToken) {
        if (!jwtUtil.isValidToken(refreshToken)) {
            throw new IllegalArgumentException(TokenError.INVALID_TOKEN.getMessage());
        }
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);
        return jwtUtil.generateToken(user, JwtProperties.ACCESS_TOKEN_EXPIRATION_DURATION);
    }

    public String createNewRefreshToken(String refreshToken) {
        if (!jwtUtil.isValidToken(refreshToken)) {
            throw new IllegalArgumentException(TokenError.INVALID_TOKEN.getMessage());
        }
        RefreshToken oldRefreshToken = refreshTokenService.findByRefreshToken(refreshToken);
        User user = userService.findById(oldRefreshToken.getUserId());
        String newRefreshToken = jwtUtil.generateToken(user,
            JwtProperties.REFRESH_TOKEN_EXPIRATION_DURATION);
        refreshTokenService.update(oldRefreshToken, newRefreshToken);
        return newRefreshToken;
    }
}
