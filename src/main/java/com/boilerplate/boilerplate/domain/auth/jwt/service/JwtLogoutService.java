package com.boilerplate.boilerplate.domain.auth.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtLogoutService {

    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    public void logout(String refreshToken) {

        if (refreshToken == null) {
            return;
        }

        if (!jwtTokenService.isValidToken(refreshToken)) {
            return;
        }

        refreshTokenService.deleteByRefreshToken(refreshToken);
    }
}
