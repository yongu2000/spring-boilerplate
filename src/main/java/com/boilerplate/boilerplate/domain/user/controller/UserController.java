package com.boilerplate.boilerplate.domain.user.controller;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.auth.jwt.service.AccessTokenService;
import com.boilerplate.boilerplate.domain.auth.jwt.service.RefreshTokenService;
import com.boilerplate.boilerplate.domain.user.dto.EmailDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.dto.PublicUserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UpdateUserProfileRequest;
import com.boilerplate.boilerplate.domain.user.dto.UserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UsernameDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import com.boilerplate.boilerplate.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final JwtConfig jwtConfig;

    @GetMapping("/my")
    public ResponseEntity<UserResponse> getUserProfile() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserProfile());
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserResponse> updateUserProfile(
        HttpServletRequest request,
        HttpServletResponse response,
        @PathVariable String username,
        @RequestBody UpdateUserProfileRequest updateRequest
    ) {

        UserResponse userResponse = userService.updateUserProfile(username, updateRequest);

        User user = userService.findByUsername(userResponse.getUsername());
        CustomUserDetails userDetails = new CustomUserDetails(user);

        boolean rememberMe = Boolean.parseBoolean(
            CookieUtil.getCookieByName(request.getCookies(), jwtConfig.getRememberMeCookieName()));
        Duration expiration =
            rememberMe ?
                jwtConfig.getRememberMeRefreshTokenExpiration() :
                jwtConfig.getRefreshTokenExpiration();

        String newAccessToken = accessTokenService.createAccessToken(userDetails);
        String newRefreshToken = refreshTokenService.createRefreshToken(userDetails, expiration);

        CookieUtil.addCookie(response, jwtConfig.getRefreshTokenCookieName(), newRefreshToken,
            (int) expiration.toSeconds());
        response.addHeader(jwtConfig.getHeaderAuthorization(),
            jwtConfig.getAccessTokenPrefix() + newAccessToken);

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<EmailDuplicateCheckResponse> checkEmailDuplicate(
        @PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.checkEmailDuplicate(email));
    }

    @GetMapping("/check/username/{username}")
    public ResponseEntity<UsernameDuplicateCheckResponse> checkUsernameDuplicate(
        @PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.checkUsernameDuplicate(username));
    }

    @GetMapping("/{username}")
    public ResponseEntity<PublicUserResponse> getPublicUserByUsername(@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getPublicUserByUsername(username));
    }
}
