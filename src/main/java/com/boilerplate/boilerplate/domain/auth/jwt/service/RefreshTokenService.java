package com.boilerplate.boilerplate.domain.auth.jwt.service;

import com.boilerplate.boilerplate.domain.auth.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.auth.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.entity.RefreshToken;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.TokenError;
import com.boilerplate.boilerplate.domain.auth.jwt.repository.RefreshTokenRepository;
import com.boilerplate.boilerplate.domain.auth.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RefreshTokenService {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public String createRefreshToken(JwtUserDetails userDetails) {
        String refreshToken = jwtTokenService.generateToken(userDetails,
            jwtProperties.getRefreshTokenExpiration());
        save(userDetails, refreshToken);
        return refreshToken;
    }

    public String createNewRefreshToken(String refreshToken) {
        if (!JwtUtil.isValidToken(refreshToken, jwtProperties.getSecretKey())) {
            throw new IllegalArgumentException(TokenError.INVALID_TOKEN.getMessage());
        }
        RefreshToken oldRefreshToken = findByRefreshToken(refreshToken);
        JwtUserDetails userDetails = new JwtUserDetails(userService.findById(oldRefreshToken.getUserId()));
        String newRefreshToken = jwtTokenService.generateToken(userDetails,
            jwtProperties.getRefreshTokenExpiration());
        update(oldRefreshToken, newRefreshToken);
        return newRefreshToken;
    }

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new EntityNotFoundException(
                TokenError.REFRESH_TOKEN_NOT_EXIST.getMessage()));
    }

    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    private void save(JwtUserDetails userDetails, String refreshToken) {
        LocalDateTime expirationTime = LocalDateTime.now()
            .plus(jwtProperties.getRefreshTokenExpiration());
        refreshTokenRepository.save(new RefreshToken(userDetails.getId(), refreshToken, expirationTime));
    }

    private void update(RefreshToken oldRefreshToken, String newRefreshToken) {
        LocalDateTime expirationTime = LocalDateTime.now()
            .plus(jwtProperties.getRefreshTokenExpiration());
        oldRefreshToken.update(newRefreshToken, expirationTime);
        refreshTokenRepository.save(oldRefreshToken);
    }


}
