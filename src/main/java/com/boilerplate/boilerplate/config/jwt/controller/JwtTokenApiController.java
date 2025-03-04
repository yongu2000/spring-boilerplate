package com.boilerplate.boilerplate.config.jwt.controller;

import com.boilerplate.boilerplate.config.jwt.dto.ReissueAccessTokenRequest;
import com.boilerplate.boilerplate.config.jwt.dto.ReissueAccessTokenResponse;
import com.boilerplate.boilerplate.config.jwt.service.JwtTokenService;
import com.boilerplate.boilerplate.config.jwt.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtTokenApiController {

    private final JwtTokenService jwtTokenService;

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_NAME = "REFRESH_TOKEN";

    private static final Duration REFRESH_TOKEN_EXPIRATION_DURATION = Duration.ofDays(14);


    @PostMapping("/api/token/json")
    public ResponseEntity<ReissueAccessTokenResponse> reissueAccessTokenAtJson(
        @RequestBody ReissueAccessTokenRequest request) {

        String newAccessToken = jwtTokenService.createNewAccessToken(request.getRefreshToken());
        String newRefreshToken = jwtTokenService.createNewRefreshToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ReissueAccessTokenResponse(newAccessToken, newRefreshToken));
    }

    @PostMapping("/api/token/header")
    public ResponseEntity<?> reissueAccessTokenAtHeader(HttpServletRequest request,
        HttpServletResponse response) {

        String refreshToken = jwtTokenService.getRefreshTokenFromCookie(request.getCookies());

        String newAccessToken = jwtTokenService.createNewAccessToken(refreshToken);
        String newRefreshToken = jwtTokenService.createNewRefreshToken(refreshToken);

        CookieUtil.addCookie(response, REFRESH_TOKEN_NAME, newRefreshToken,
            (int) REFRESH_TOKEN_EXPIRATION_DURATION.toSeconds());
        response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + newAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
