package com.boilerplate.boilerplate.config.jwt;

import com.boilerplate.boilerplate.config.jwt.utils.JwtUtil;
import com.boilerplate.boilerplate.domain.user.entity.User;
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

    private final JwtUtil jwtUtil;
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String accessToken = getAccessToken(authorizationHeader);
        if (jwtUtil.isValidToken(accessToken)) {
            User user = User.builder()
                .username(jwtUtil.getUsername(accessToken))
                .password("")
                .role(jwtUtil.getRole(accessToken))
                .build();
            Authentication authToken = new UsernamePasswordAuthenticationToken(user,
                null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
