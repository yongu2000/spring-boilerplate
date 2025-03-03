package com.boilerplate.boilerplate.config.jwt.service;

import com.boilerplate.boilerplate.config.jwt.utils.CookieUtil;
import com.boilerplate.boilerplate.config.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    private static final Duration ACCESS_TOKEN_EXPIRATION_DURATION = Duration.ofMinutes(10);
    private static final String REFRESH_TOKEN_NAME = "REFRESH_TOKEN";

    public String getRefreshTokenFromCookie(Cookie[] cookies) {
        return CookieUtil.getCookieByName(cookies, REFRESH_TOKEN_NAME);
    }

    public String createNewAccessToken(String refreshToken) {
        if (!jwtUtil.isValidToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid Token");
        }
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);
        return jwtUtil.generateToken(user, ACCESS_TOKEN_EXPIRATION_DURATION);
    }
}
