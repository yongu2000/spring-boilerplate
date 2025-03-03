package com.boilerplate.boilerplate.config.jwt.service;

import com.boilerplate.boilerplate.config.jwt.entity.RefreshToken;
import com.boilerplate.boilerplate.config.jwt.repository.RefreshTokenRepository;
import com.boilerplate.boilerplate.domain.user.entity.User;
import java.time.LocalDateTime;
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
