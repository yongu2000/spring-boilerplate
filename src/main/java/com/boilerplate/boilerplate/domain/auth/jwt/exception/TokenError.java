package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import com.boilerplate.boilerplate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum TokenError implements ErrorCode {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "Refresh Token 이 존재하지 않습니다");

    private final HttpStatus status;
    private final String message;
}
