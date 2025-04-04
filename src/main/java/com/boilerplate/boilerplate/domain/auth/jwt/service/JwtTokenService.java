package com.boilerplate.boilerplate.domain.auth.jwt.service;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.constant.Claim;
import com.boilerplate.boilerplate.domain.auth.jwt.constant.TokenType;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.InvalidTokenTypeException;
import com.boilerplate.boilerplate.domain.auth.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class JwtTokenService {

    private final JwtConfig jwtConfig;
    private final UserService userService;

    private static final String HEADER_JWT = "JWT";

    public Date getExpirationDate(TokenType tokenType, boolean rememberMe) {

    }

    public String createToken(TokenType tokenType, CustomUserDetails userDetails) {
        return switch (tokenType) {
            case TokenType.ACCESS -> createAccessToken(userDetails);
            case TokenType.REFRESH -> createRefreshToken(userDetails);
            default -> throw new InvalidTokenTypeException();
        };
    }

    private String createRefreshToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration().toMillis());
        return Jwts.builder()
            .header().type(HEADER_JWT).and()
            .claim(Claim.TOKEN_TYPE.getValue(), TokenType.REFRESH.name())
            .claim(Claim.USERNAME.getValue(), userDetails.getUsername())
            .issuer(jwtConfig.getIssuer())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(jwtConfig.getSecretKey())
            .compact();
    }

    private String createAccessToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration().toMillis());
        return Jwts.builder()
            .header().type(HEADER_JWT).and()
            .claim(Claim.TOKEN_TYPE.getValue(), TokenType.ACCESS.name())
            .claim(Claim.USERNAME.getValue(), userDetails.getUsername())
            .issuer(jwtConfig.getIssuer())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(jwtConfig.getSecretKey())
            .compact();
    }

    public boolean isValidToken(String token) {
        return JwtUtil.isValidToken(token, jwtConfig.getSecretKey());
    }

    public CustomUserDetails getUserDetailsFromToken(String token) {
        String username = JwtUtil.getUsername(token, jwtConfig.getSecretKey());
        return new CustomUserDetails(userService.findByUsername(username));  // 사용자를 찾는 로직
    }
}
