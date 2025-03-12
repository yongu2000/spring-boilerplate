package com.boilerplate.boilerplate.domain.auth.jwt.utils;

import com.boilerplate.boilerplate.domain.auth.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.auth.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private static final String HEADER_JWT = "JWT";
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLE = "role";

    public String generateToken(JwtUserDetails userDetails, Duration expiredAt) {
        Date now = new Date();
        return createJwt(new Date(now.getTime() + expiredAt.toMillis()), userDetails);
    }

    private String createJwt(Date expiration, JwtUserDetails userDetails) {
        Date now = new Date();
        return Jwts.builder()
            .header().type(HEADER_JWT).and()
            .claim(CLAIM_ID, userDetails.getId())
            .claim(CLAIM_EMAIL, userDetails.getEmail())
            .claim(CLAIM_USERNAME, userDetails.getUsername())
            .claim(CLAIM_NAME, userDetails.getName())
            .claim(CLAIM_ROLE, userDetails.getRole())
            .issuer(jwtProperties.getIssuer())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(jwtProperties.getSecretKey())
            .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {

        return Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build().parseSignedClaims(token).getPayload()
            .get(CLAIM_ID, Long.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build().parseSignedClaims(token).getPayload()
            .get(CLAIM_EMAIL, String.class);
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build().parseSignedClaims(token).getPayload()
            .get(CLAIM_USERNAME, String.class);
    }

    public String getName(String token) {
        return Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build().parseSignedClaims(token).getPayload()
            .get(CLAIM_NAME, String.class);
    }

    public Role getRole(String token) {

        return Role.of(
            Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build().parseSignedClaims(token).getPayload()
                .get(CLAIM_ROLE, String.class));
    }


}
