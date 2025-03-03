package com.boilerplate.boilerplate.config.jwt.service;

import com.boilerplate.boilerplate.config.jwt.entity.RefreshToken;
import com.boilerplate.boilerplate.config.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("No Such RefreshToken"));
    }

}
