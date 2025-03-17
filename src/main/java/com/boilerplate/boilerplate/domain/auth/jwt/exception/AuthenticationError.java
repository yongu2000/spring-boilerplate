package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import com.boilerplate.boilerplate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AuthenticationError implements ErrorCode {

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    INVALID_JSON_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 JSON 형식입니다."),
    MISSING_LOGIN_INFO(HttpStatus.BAD_REQUEST, "로그인 정보가 누락되었습니다.");

    private final HttpStatus status;
    private final String message;
}
