package com.boilerplate.boilerplate.domain.user.controller;

import com.boilerplate.boilerplate.domain.user.dto.UserResponse;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getCurrentUser() {
        log.info("유저 정보 조회");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getCurrentUser());
    }
}
