package com.boilerplate.boilerplate.domain.auth.jwt.service;

import com.boilerplate.boilerplate.domain.auth.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.InvalidRefreshTokenException;
import com.boilerplate.boilerplate.domain.auth.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccessTokenService {

    private final JwtConfig jwtConfig;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createAccessToken(JwtUserDetails userDetails) {
        return jwtTokenService.generateToken(userDetails, jwtConfig.getAccessTokenExpiration());
    }

    public String reissueAccessToken(String refreshToken) {
        if (!JwtUtil.isValidToken(refreshToken, jwtConfig.getSecretKey())) {
            throw new InvalidRefreshTokenException();
        }
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        JwtUserDetails userDetails = new JwtUserDetails(userService.findById(userId));
        return jwtTokenService.generateToken(userDetails, jwtConfig.getAccessTokenExpiration());
    }

}
