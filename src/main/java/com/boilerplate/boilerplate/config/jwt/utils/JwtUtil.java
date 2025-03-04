package com.boilerplate.boilerplate.config.jwt.utils;

import com.boilerplate.boilerplate.config.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    private static final String HEADER_JWT = "JWT";
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_Role = "role";

    private JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = new SecretKeySpec(
            jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
            Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return createJwt(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    private String createJwt(Date expiration, User user) {
        Date now = new Date();
        return Jwts.builder()
            .header().type(HEADER_JWT).and()
            .claim(CLAIM_ID, user.getId())
            .claim(CLAIM_USERNAME, user.getUsername())
            .claim(CLAIM_Role, user.getRole())
            .issuer(jwtProperties.getIssuer())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get(CLAIM_ID, Long.class);
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get(CLAIM_USERNAME, String.class);
    }

    public Role getRole(String token) {

        return Role.of(
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get(CLAIM_Role, String.class));
    }


}
