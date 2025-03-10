package com.boilerplate.boilerplate.global.auth.jwt.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenError {

    INVALID_TOKEN("유효하지 않은 토큰입니다"),
    REFRESH_TOKEN_NOT_EXIST("Refresh 토큰이 존재하지 않습니다");

    private final String message;
}
