package com.boilerplate.boilerplate.domain.auth.jwt.service;

import com.boilerplate.boilerplate.domain.auth.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.auth.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.entity.RefreshToken;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.TokenError;
import com.boilerplate.boilerplate.domain.auth.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class JwtTokenService {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String getRefreshTokenFromCookie(Cookie[] cookies) {
        return CookieUtil.getCookieByName(cookies, jwtProperties.getRefreshTokenName());
    }

    public boolean isValidToken(String token) {
        return jwtUtil.isValidToken(token);
    }

    public JwtUserDetails getUserDetailsFromToken(String token) {
        String username = jwtUtil.getUsername(token);
        return new JwtUserDetails(userService.findByUsername(username));  // 사용자를 찾는 로직
    }

    public String createAccessToken(JwtUserDetails userDetails) {
        return jwtUtil.generateToken(userDetails, jwtProperties.getAccessTokenExpiration());
    }

    public String createRefreshToken(JwtUserDetails userDetails) {
        String refreshToken = jwtUtil.generateToken(userDetails,
            jwtProperties.getRefreshTokenExpiration());
        refreshTokenService.save(userDetails, refreshToken);
        return refreshToken;
    }

    public String createNewAccessToken(String refreshToken) {
        if (!jwtUtil.isValidToken(refreshToken)) {
            throw new IllegalArgumentException(TokenError.INVALID_TOKEN.getMessage());
        }
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        JwtUserDetails userDetails = new JwtUserDetails(userService.findById(userId));
        return jwtUtil.generateToken(userDetails, jwtProperties.getAccessTokenExpiration());
    }

    public String createNewRefreshToken(String refreshToken) {
        if (!jwtUtil.isValidToken(refreshToken)) {
            throw new IllegalArgumentException(TokenError.INVALID_TOKEN.getMessage());
        }
        RefreshToken oldRefreshToken = refreshTokenService.findByRefreshToken(refreshToken);
        JwtUserDetails userDetails = new JwtUserDetails(userService.findById(oldRefreshToken.getUserId()));
        String newRefreshToken = jwtUtil.generateToken(userDetails,
            jwtProperties.getRefreshTokenExpiration());
        refreshTokenService.update(oldRefreshToken, newRefreshToken);
        return newRefreshToken;
    }

}
