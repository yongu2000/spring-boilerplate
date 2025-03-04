package com.boilerplate.boilerplate.config.jwt.service;

import com.boilerplate.boilerplate.config.jwt.entity.RefreshToken;
import com.boilerplate.boilerplate.config.jwt.exception.TokenError;
import com.boilerplate.boilerplate.config.jwt.repository.RefreshTokenRepository;
import com.boilerplate.boilerplate.domain.user.entity.User;
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
            .orElseThrow(() -> new IllegalArgumentException(
                TokenError.REFRESH_TOKEN_NOT_EXIST.getMessage()));
    }

    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    public void save(User user, String refreshToken) {
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(14);
        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken, expirationTime));
    }

    public void update(RefreshToken oldRefreshToken, String newRefreshToken) {
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(14);
        oldRefreshToken.update(newRefreshToken, expirationTime);
        refreshTokenRepository.save(oldRefreshToken);
    }
}
