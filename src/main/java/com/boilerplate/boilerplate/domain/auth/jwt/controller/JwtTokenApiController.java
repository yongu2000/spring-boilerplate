package com.boilerplate.boilerplate.domain.auth.jwt.controller;

import com.boilerplate.boilerplate.domain.auth.jwt.service.AccessTokenService;
import com.boilerplate.boilerplate.domain.auth.jwt.service.RefreshTokenService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class JwtTokenApiController {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
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

        String refreshToken = CookieUtil.getCookieByName(request.getCookies(), jwtConfig.getRefreshTokenName());

        String newAccessToken = accessTokenService.reissueAccessToken(refreshToken);
        String newRefreshToken = refreshTokenService.reissueRefreshToken(refreshToken);

        CookieUtil.addCookie(response, jwtConfig.getRefreshTokenName(), newRefreshToken,
            (int) jwtConfig.getRefreshTokenExpiration().toSeconds());
        response.addHeader(jwtConfig.getHeaderAuthorization(),
            jwtConfig.getAccessTokenPrefix() + newAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
