package com.boilerplate.boilerplate.global.auth.jwt.service;

import com.boilerplate.boilerplate.config.JwtProperties;
import com.boilerplate.boilerplate.global.auth.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.global.auth.jwt.entity.RefreshToken;
import com.boilerplate.boilerplate.global.auth.jwt.exception.TokenError;
import com.boilerplate.boilerplate.global.auth.jwt.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new EntityNotFoundException(
                TokenError.REFRESH_TOKEN_NOT_EXIST.getMessage()));
    }

    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    public void save(JwtUserDetails userDetails, String refreshToken) {
        LocalDateTime expirationTime = LocalDateTime.now()
            .plus(JwtProperties.REFRESH_TOKEN_EXPIRATION_DURATION);
        refreshTokenRepository.save(new RefreshToken(userDetails.getId(), refreshToken, expirationTime));
    }

    public void update(RefreshToken oldRefreshToken, String newRefreshToken) {
        LocalDateTime expirationTime = LocalDateTime.now()
            .plus(JwtProperties.REFRESH_TOKEN_EXPIRATION_DURATION);
        oldRefreshToken.update(newRefreshToken, expirationTime);
        refreshTokenRepository.save(oldRefreshToken);
    }
}
