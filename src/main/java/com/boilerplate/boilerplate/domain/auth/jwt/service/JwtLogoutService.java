package com.boilerplate.boilerplate.domain.auth.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtLogoutService {

    private final JwtTokenService jwtTokenService;

    public void logout(String refreshToken) {

        if (refreshToken == null) {
            return;
        }

        if (!jwtTokenService.isValidRefreshToken(refreshToken)) {
            return;
        }

        jwtTokenService.deleteRefreshToken(refreshToken);
    }
}
