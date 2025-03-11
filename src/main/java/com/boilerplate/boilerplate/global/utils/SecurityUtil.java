package com.boilerplate.boilerplate.global.utils;

import com.boilerplate.boilerplate.domain.auth.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.AuthenticationError;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(AuthenticationError.AUTHENTICATION_FAILURE.getMessage());
        }

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
