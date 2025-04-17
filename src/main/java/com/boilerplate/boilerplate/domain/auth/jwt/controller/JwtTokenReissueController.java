package com.boilerplate.boilerplate.domain.auth.jwt.controller;

import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtTokenService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Slf4j
public class JwtTokenReissueController {

    private final JwtTokenService jwtTokenService;
    private final JwtConfig jwtConfig;

//    @PostMapping("/json")
//    public ResponseEntity<ReissueAccessTokenResponse> reissueAccessTokenAtJson(
//        @RequestBody ReissueAccessTokenRequest request) {
//
//        String newAccessToken = accessTokenService.createNewAccessToken(request.getRefreshToken());
//        String newRefreshToken = refreshTokenService.createNewRefreshToken(request.getRefreshToken());
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//            .body(new ReissueAccessTokenResponse(newAccessToken, newRefreshToken));
//    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueAccessTokenAtHeader(HttpServletRequest request,
        HttpServletResponse response) {

        String refreshToken = CookieUtil.getCookieByName(request.getCookies(),
            jwtConfig.getRefreshTokenCookieName());
        log.info("client refresh token = {}", refreshToken);
        String newAccessToken = jwtTokenService.reissueAccessToken(refreshToken);

        log.info("new access token = {}", newAccessToken);

        String newRefreshToken = jwtTokenService.reissueRefreshToken(refreshToken);

        log.info("new refresh token = {}", newRefreshToken);

        jwtTokenService.setAccessToken(response, newAccessToken);
        jwtTokenService.setRefreshToken(response, newRefreshToken);

        log.info("Token Set");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
