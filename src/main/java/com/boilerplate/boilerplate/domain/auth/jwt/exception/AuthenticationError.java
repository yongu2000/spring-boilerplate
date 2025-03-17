package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import com.boilerplate.boilerplate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AuthenticationError implements ErrorCode {

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다");

    private final HttpStatus status;
    private final String message;
}
