package com.boilerplate.boilerplate.domain.auth.jwt.controller;

import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtTokenService;
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

        String newAccessToken = jwtTokenService.reissueAccessToken(refreshToken);
        String newRefreshToken = jwtTokenService.reissueRefreshToken(refreshToken);

        jwtTokenService.setAccessToken(response, newAccessToken);
        jwtTokenService.setRefreshToken(response, newRefreshToken);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
