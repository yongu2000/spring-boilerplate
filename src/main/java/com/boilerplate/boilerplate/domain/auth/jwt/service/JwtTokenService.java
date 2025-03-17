package com.boilerplate.boilerplate.domain.auth.jwt.service;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.constant.Claim;
import com.boilerplate.boilerplate.domain.auth.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
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

    public String generateToken(CustomUserDetails userDetails, Duration expiredAt) {
        Date now = new Date();
        return createJwt(new Date(now.getTime() + expiredAt.toMillis()), userDetails);
    }

    private String createJwt(Date expiration, CustomUserDetails userDetails) {
        Date now = new Date();
        return Jwts.builder()
            .header().type(HEADER_JWT).and()
            .claim(Claim.ID.getValue(), userDetails.getId())
            .claim(Claim.EMAIL.getValue(), userDetails.getEmail())
            .claim(Claim.USERNAME.getValue(), userDetails.getUsername())
            .claim(Claim.NAME.getValue(), userDetails.getName())
            .claim(Claim.ROLE.getValue(), userDetails.getRole())
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
