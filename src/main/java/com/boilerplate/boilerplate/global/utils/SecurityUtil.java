package com.boilerplate.boilerplate.global.utils;

import com.boilerplate.boilerplate.global.auth.jwt.entity.JwtUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("사용자가 인증되지 않았습니다.");
        }

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
