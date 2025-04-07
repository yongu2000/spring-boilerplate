package com.boilerplate.boilerplate.domain.auth.jwt.controller;

import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logout")
@RequiredArgsConstructor
public class JwtLogoutController {

    private final JwtTokenService jwtTokenService;

    @PostMapping
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        jwtTokenService.logout(request, response);
        return ResponseEntity.ok().build();
    }

}
