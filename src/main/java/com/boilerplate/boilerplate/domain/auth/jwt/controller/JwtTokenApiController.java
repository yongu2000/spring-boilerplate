package com.boilerplate.boilerplate.domain.auth.jwt.controller;

import com.boilerplate.boilerplate.domain.auth.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.auth.jwt.dto.ReissueAccessTokenRequest;
import com.boilerplate.boilerplate.domain.auth.jwt.dto.ReissueAccessTokenResponse;
import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtTokenService;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class JwtTokenApiController {

    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;

    @PostMapping("/json")
    public ResponseEntity<ReissueAccessTokenResponse> reissueAccessTokenAtJson(
        @RequestBody ReissueAccessTokenRequest request) {

        String newAccessToken = jwtTokenService.createNewAccessToken(request.getRefreshToken());
        String newRefreshToken = jwtTokenService.createNewRefreshToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ReissueAccessTokenResponse(newAccessToken, newRefreshToken));
    }

    @PostMapping("/header")
    public ResponseEntity<?> reissueAccessTokenAtHeader(HttpServletRequest request,
        HttpServletResponse response) {

        String refreshToken = jwtTokenService.getRefreshTokenFromCookie(request.getCookies());

        String newAccessToken = jwtTokenService.createNewAccessToken(refreshToken);
        String newRefreshToken = jwtTokenService.createNewRefreshToken(refreshToken);

        CookieUtil.addCookie(response, jwtProperties.getRefreshTokenName(), newRefreshToken,
            (int) jwtProperties.getRefreshTokenExpiration().toSeconds());
        response.addHeader(jwtProperties.getHeaderAuthorization(),
            jwtProperties.getAccessTokenPrefix() + newAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
