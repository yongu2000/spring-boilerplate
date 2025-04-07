package com.boilerplate.boilerplate.domain.email.controller;

import com.boilerplate.boilerplate.domain.email.dto.SendVerificationEmailRequest;
import com.boilerplate.boilerplate.domain.email.dto.VerifyCodeRequest;
import com.boilerplate.boilerplate.domain.email.dto.VerifyCodeResponse;
import com.boilerplate.boilerplate.domain.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send/code")
    public ResponseEntity<?> sendVerificationEmail(
        @Valid @RequestBody SendVerificationEmailRequest request) {
        emailService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify/code")
    public ResponseEntity<VerifyCodeResponse> verifyCode(
        @Valid @RequestBody VerifyCodeRequest request) {
        return ResponseEntity.ok(emailService.verifyCode(request.getEmail(), request.getCode()));
    }
}
