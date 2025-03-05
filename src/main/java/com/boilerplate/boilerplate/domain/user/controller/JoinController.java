package com.boilerplate.boilerplate.domain.user.controller;

import com.boilerplate.boilerplate.domain.user.dto.JoinRequest;
import com.boilerplate.boilerplate.domain.user.dto.JoinResponse;
import com.boilerplate.boilerplate.domain.user.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/join")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping
    public ResponseEntity<?> join(@RequestBody JoinRequest request) {
        JoinResponse response = joinService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
