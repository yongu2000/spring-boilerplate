package com.boilerplate.boilerplate.domain.auth.jwt.utils;

import com.boilerplate.boilerplate.domain.auth.jwt.constant.Claim;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtUtil {

    public static boolean isValidToken(String token, SecretKey secretKey) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Long getUserId(String token, SecretKey secretKey) {
        return getPayload(token, secretKey).get(Claim.ID.getValue(), Long.class);
    }

    public static String getEmail(String token, SecretKey secretKey) {
        return getPayload(token, secretKey).get(Claim.EMAIL.getValue(), String.class);
    }

    public static String getUsername(String token, SecretKey secretKey) {
        return getPayload(token, secretKey).get(Claim.USERNAME.getValue(), String.class);
    }

    public static String getName(String token, SecretKey secretKey) {
        return getPayload(token, secretKey).get(Claim.NAME.getValue(), String.class);
    }

    public static Role getRole(String token, SecretKey secretKey) {
        return Role.valueOf(getPayload(token, secretKey).get(Claim.ROLE.getValue(), String.class));
    }

    public static Date getExpiration(String token, SecretKey secretKey) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public static Claims getClaims(String token, SecretKey secretKey) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    private static Claims getPayload(String token, SecretKey secretKey) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

}
