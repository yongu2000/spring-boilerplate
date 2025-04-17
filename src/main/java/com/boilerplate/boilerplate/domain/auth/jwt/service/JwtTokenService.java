package com.boilerplate.boilerplate.domain.auth.jwt.service;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.constant.Claim;
import com.boilerplate.boilerplate.domain.auth.jwt.constant.TokenType;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.InvalidRefreshTokenException;
import com.boilerplate.boilerplate.domain.auth.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class JwtTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private final JwtConfig jwtConfig;
    private final UserService userService;

    private static final String HEADER_JWT = "JWT";

    public String createAccessToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiration = new Date(
            now.getTime() + jwtConfig.getAccessTokenExpiration().toMillis());

        return Jwts.builder()
            .header().type(HEADER_JWT).and()
            .claim(Claim.TOKEN_TYPE.getValue(), TokenType.ACCESS.name())
            .claim(Claim.ID.getValue(), userDetails.getId())
            .issuer(jwtConfig.getIssuer())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(jwtConfig.getSecretKey())
            .compact();
    }

    public String createRefreshToken(CustomUserDetails userDetails, boolean rememberMe) {
        Date now = new Date();
        Duration expirationDuration = rememberMe ? jwtConfig.getRememberMeRefreshTokenExpiration()
            : jwtConfig.getRefreshTokenExpiration();
        Date expiration = new Date(now.getTime() + expirationDuration.toMillis());

        String token = Jwts.builder()
            .header().type(HEADER_JWT).and()
            .claim(Claim.TOKEN_TYPE.getValue(), TokenType.REFRESH.name())
            .claim(Claim.ID.getValue(), userDetails.getId())
            .issuer(jwtConfig.getIssuer())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(jwtConfig.getSecretKey())
            .compact();

        saveRefreshToken(token, userDetails.getId());
        return token;
    }

    public String createRefreshToken(CustomUserDetails userDetails, Date expiration) {
        Date now = new Date();
        String token = Jwts.builder()
            .header().type(HEADER_JWT).and()
            .claim(Claim.TOKEN_TYPE.getValue(), TokenType.REFRESH.name())
            .claim(Claim.ID.getValue(), userDetails.getId())
            .issuer(jwtConfig.getIssuer())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(jwtConfig.getSecretKey())
            .compact();

        saveRefreshToken(token, userDetails.getId());
        return token;
    }

    public String reissueAccessToken(String refreshToken) {
        if (!isValidRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        Long userId = getUserIdByRefreshToken(refreshToken);
        CustomUserDetails userDetails = new CustomUserDetails(userService.findById(userId));
        return createAccessToken(userDetails);
    }

    public String reissueRefreshToken(String refreshToken) {
        if (!isValidRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        CustomUserDetails userDetails = getUserDetailsFromToken(refreshToken);
        Date expiration = JwtUtil.getExpiration(refreshToken, jwtConfig.getSecretKey());

        String newRefreshToken = createRefreshToken(userDetails, expiration);
        deleteRefreshToken(refreshToken);

        saveRefreshToken(newRefreshToken, userDetails.getId());
        return newRefreshToken;
    }

    public void setAccessToken(HttpServletResponse response, String accessToken) {
        response.addHeader(jwtConfig.getHeaderAuthorization(),
            jwtConfig.getAccessTokenPrefix() + accessToken);
    }

    public void setRefreshToken(HttpServletResponse response, String refreshToken) {
        Date expiration = JwtUtil.getExpiration(refreshToken, jwtConfig.getSecretKey());
        long ttlInSeconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;

        int maxAge = (int) Math.max(ttlInSeconds, 0);

        CookieUtil.addCookie(response,
            jwtConfig.getRefreshTokenCookieName(),
            refreshToken,
            maxAge);
    }

    public boolean isValidAccessToken(String accessToken) {
        try {
            return JwtUtil.getTokenType(accessToken, jwtConfig.getSecretKey()) == TokenType.ACCESS;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidRefreshToken(String refreshToken) {
        try {
            log.info("🔍 검사할 리프레시 토큰: {}", refreshToken);
            String key = "RT:" + refreshToken;
            boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
            log.info("🔎 Redis에서 키 [{}] 존재 여부: {}", key, exists);
            return JwtUtil.getTokenType(refreshToken, jwtConfig.getSecretKey()) == TokenType.REFRESH
                && exists;
        } catch (Exception e) {
            return false;
        }
    }

    public CustomUserDetails getUserDetailsFromToken(String token) {
        Long userId = JwtUtil.getId(token, jwtConfig.getSecretKey());
        return new CustomUserDetails(userService.findById(userId));  // 사용자를 찾는 로직
    }

    private void saveRefreshToken(String refreshToken, Long userId) {
        Date expiration = JwtUtil.getExpiration(refreshToken, jwtConfig.getSecretKey());
        long ttlInMillis = expiration.getTime() - System.currentTimeMillis();

        redisTemplate.opsForValue().set(
            "RT:" + refreshToken,
            String.valueOf(userId),
            Duration.ofMillis(ttlInMillis)
        );

        boolean check = Boolean.TRUE.equals(redisTemplate.hasKey("RT:" + refreshToken));
        log.info("✅ Redis에 저장 확인: {}", check);  // true 나와야 정상
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete("RT:" + refreshToken);
    }

    private Long getUserIdByRefreshToken(String refreshToken) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("RT:" + refreshToken))
            .map(Long::parseLong)
            .orElseThrow(InvalidRefreshTokenException::new);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieByName(request.getCookies(),
            jwtConfig.getRefreshTokenCookieName());
        if (refreshToken == null) {
            return;
        }
        if (!isValidRefreshToken(refreshToken)) {
            return;
        }
        deleteRefreshToken(refreshToken);
        CookieUtil.deleteCookie(request, response, jwtConfig.getRefreshTokenCookieName());
    }
}
