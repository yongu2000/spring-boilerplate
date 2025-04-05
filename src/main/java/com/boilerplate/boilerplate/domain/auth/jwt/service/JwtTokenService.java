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
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
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
        deleteRefreshToken(refreshToken);
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
        return JwtUtil.getTokenType(accessToken, jwtConfig.getSecretKey()) == TokenType.ACCESS;
    }

    public boolean isValidRefreshToken(String refreshToken) {
        return JwtUtil.getTokenType(refreshToken, jwtConfig.getSecretKey()) == TokenType.REFRESH
            && redisTemplate.hasKey("RT:" + refreshToken);
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
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete("RT:" + refreshToken);
    }

    private Long getUserIdByRefreshToken(String refreshToken) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("RT:" + refreshToken))
            .map(Long::parseLong)
            .orElseThrow(InvalidRefreshTokenException::new);
    }

    //    public String createToken(TokenType tokenType, CustomUserDetails userDetails) {
//        String token = createTokenWithType(tokenType, getExpirationByType(tokenType),
//            userDetails);
//        if (tokenType != TokenType.ACCESS) {
//            saveRefreshToken(token, userDetails.getId());
//        }
//        return token;
//    }
//
//    public String reissueToken(TokenType tokenType, String refreshToken) {
//        if (!JwtUtil.isValidToken(refreshToken, jwtConfig.getSecretKey())) {
//            throw new InvalidRefreshTokenException();
//        }
//        Long userId = getUserIdByRefreshToken(refreshToken);
//        CustomUserDetails userDetails = new CustomUserDetails(userService.findById(userId));
//        return switch (tokenType) {
//            case ACCESS -> createToken(tokenType, userDetails);
//            case REFRESH, REMEMBER_ME_REFRESH -> reissueRefreshToken(userDetails, refreshToken);
//        };
//    }
//
//    private String reissueRefreshToken(UserDetails userDetails, String refreshToken) {
//        Date expiration = JwtUtil.getExpiration(refreshToken, jwtConfig.getSecretKey());
//        long ttlInMillis = expiration.getTime() - System.currentTimeMillis();
//    }
//

//
//    private String createTokenWithType(TokenType tokenType, Duration expirationDuration,
//        CustomUserDetails userDetails) {
//        Date now = new Date();
//        Date expiration = new Date(now.getTime() + expirationDuration.toMillis());
//
//        return Jwts.builder()
//            .header().type(HEADER_JWT).and()
//            .claim(Claim.TOKEN_TYPE.getValue(), tokenType.name())
//            .claim(Claim.ID.getValue(), userDetails.getId())
//            .issuer(jwtConfig.getIssuer())
//            .issuedAt(now)
//            .expiration(expiration)
//            .signWith(jwtConfig.getSecretKey())
//            .compact();
//    }
//
//    private Duration getExpirationByType(TokenType tokenType) {
//        return switch (tokenType) {
//            case ACCESS -> jwtConfig.getAccessTokenExpiration();
//            case REFRESH -> jwtConfig.getRefreshTokenExpiration();
//            case REMEMBER_ME_REFRESH -> jwtConfig.getRememberMeRefreshTokenExpiration();
//        };
//    }
//
//    private String createJwt(Date expiration, CustomUserDetails userDetails) {
//        Date now = new Date();
//        return Jwts.builder()
//            .header().type(HEADER_JWT).and()
//            .claim(Claim.ID.getValue(), userDetails.getId())
//            .issuer(jwtConfig.getIssuer())
//            .issuedAt(now)
//            .expiration(expiration)
//            .signWith(jwtConfig.getSecretKey())
//            .compact();
//    }
//


}
