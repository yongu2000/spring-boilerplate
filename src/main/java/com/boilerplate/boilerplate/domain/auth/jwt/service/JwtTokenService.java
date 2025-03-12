package com.boilerplate.boilerplate.domain.auth.jwt.service;

import com.boilerplate.boilerplate.domain.auth.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.auth.jwt.constant.Claim;
import com.boilerplate.boilerplate.domain.auth.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.service.UserService;
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

    private final JwtProperties jwtProperties;
    private final UserService userService;

    private static final String HEADER_JWT = "JWT";

    public String generateToken(JwtUserDetails userDetails, Duration expiredAt) {
        Date now = new Date();
        return createJwt(new Date(now.getTime() + expiredAt.toMillis()), userDetails);
    }

    private String createJwt(Date expiration, JwtUserDetails userDetails) {
        Date now = new Date();
        return Jwts.builder()
            .header().type(HEADER_JWT).and()
            .claim(Claim.ID.getValue(), userDetails.getId())
            .claim(Claim.EMAIL.getValue(), userDetails.getEmail())
            .claim(Claim.USERNAME.getValue(), userDetails.getUsername())
            .claim(Claim.NAME.getValue(), userDetails.getName())
            .claim(Claim.ROLE.getValue(), userDetails.getRole())
            .issuer(jwtProperties.getIssuer())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(jwtProperties.getSecretKey())
            .compact();
    }

    public boolean isValidToken(String token) {
        return JwtUtil.isValidToken(token, jwtProperties.getSecretKey());
    }

    public JwtUserDetails getUserDetailsFromToken(String token) {
        String username = JwtUtil.getUsername(token, jwtProperties.getSecretKey());
        return new JwtUserDetails(userService.findByUsername(username));  // 사용자를 찾는 로직
    }
}
