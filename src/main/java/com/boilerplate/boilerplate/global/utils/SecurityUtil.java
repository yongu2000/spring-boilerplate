package com.boilerplate.boilerplate.global.utils;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.AuthenticationFailedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationFailedException();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
