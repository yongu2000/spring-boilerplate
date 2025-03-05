package com.boilerplate.boilerplate.config.jwt.filters;

import com.boilerplate.boilerplate.config.jwt.JwtProperties;
import com.boilerplate.boilerplate.config.jwt.JwtUserDetails;
import com.boilerplate.boilerplate.config.jwt.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(JwtProperties.HEADER_AUTHORIZATION);
        String accessToken = getAccessToken(authorizationHeader);
        if (accessToken != null && jwtTokenService.isValidToken(accessToken)) {
            JwtUserDetails userDetails = new JwtUserDetails(jwtTokenService.getUserFromToken(accessToken));
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(
            JwtProperties.ACCESS_TOKEN_PREFIX)) {
            return authorizationHeader.substring(JwtProperties.ACCESS_TOKEN_PREFIX.length());
        }
        return null;
    }
}
