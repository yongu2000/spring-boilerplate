package com.boilerplate.boilerplate.domain.auth.jwt.service;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.entity.RefreshToken;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.InvalidRefreshTokenException;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.RefreshTokenNotFoundException;
import com.boilerplate.boilerplate.domain.auth.jwt.repository.RefreshTokenRepository;
import com.boilerplate.boilerplate.domain.auth.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RefreshTokenService {

    private final JwtConfig jwtConfig;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public String createRefreshToken(CustomUserDetails userDetails) {
        String refreshToken = jwtTokenService.generateToken(userDetails,
            jwtConfig.getRefreshTokenExpiration());
        save(userDetails, refreshToken);
        return refreshToken;
    }

    public String reissueRefreshToken(String refreshToken) {
        if (!JwtUtil.isValidToken(refreshToken, jwtConfig.getSecretKey())) {
            throw new InvalidRefreshTokenException();
        }
        RefreshToken oldRefreshToken = findByRefreshToken(refreshToken);
        CustomUserDetails userDetails = new CustomUserDetails(userService.findById(oldRefreshToken.getUserId()));
        String newRefreshToken = jwtTokenService.generateToken(userDetails,
            jwtConfig.getRefreshTokenExpiration());
        delete(oldRefreshToken);
        save(userDetails, newRefreshToken);
        return newRefreshToken;
    }

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(RefreshTokenNotFoundException::new);
    }

    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    private void save(CustomUserDetails userDetails, String refreshToken) {
        LocalDateTime expirationTime = LocalDateTime.now()
            .plus(jwtConfig.getRefreshTokenExpiration());
        refreshTokenRepository.save(new RefreshToken(userDetails.getId(), refreshToken, expirationTime));
    }

    private void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

}
