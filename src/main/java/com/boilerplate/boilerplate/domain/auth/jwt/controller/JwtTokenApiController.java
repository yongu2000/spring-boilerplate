package com.boilerplate.boilerplate.domain.auth.jwt.controller;

import com.boilerplate.boilerplate.domain.auth.jwt.JwtProperties;
import com.boilerplate.boilerplate.domain.auth.jwt.dto.ReissueAccessTokenRequest;
import com.boilerplate.boilerplate.domain.auth.jwt.dto.ReissueAccessTokenResponse;
import com.boilerplate.boilerplate.domain.auth.jwt.service.AccessTokenService;
import com.boilerplate.boilerplate.domain.auth.jwt.service.RefreshTokenService;
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

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    @PostMapping("/json")
    public ResponseEntity<ReissueAccessTokenResponse> reissueAccessTokenAtJson(
        @RequestBody ReissueAccessTokenRequest request) {

        String newAccessToken = accessTokenService.createNewAccessToken(request.getRefreshToken());
        String newRefreshToken = refreshTokenService.createNewRefreshToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ReissueAccessTokenResponse(newAccessToken, newRefreshToken));
    }

    @PostMapping("/header")
    public ResponseEntity<?> reissueAccessTokenAtHeader(HttpServletRequest request,
        HttpServletResponse response) {

        String refreshToken = CookieUtil.getCookieByName(request.getCookies(), jwtProperties.getRefreshTokenName());

        String newAccessToken = accessTokenService.createNewAccessToken(refreshToken);
        String newRefreshToken = refreshTokenService.createNewRefreshToken(refreshToken);

        CookieUtil.addCookie(response, jwtProperties.getRefreshTokenName(), newRefreshToken,
            (int) jwtProperties.getRefreshTokenExpiration().toSeconds());
        response.addHeader(jwtProperties.getHeaderAuthorization(),
            jwtProperties.getAccessTokenPrefix() + newAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
