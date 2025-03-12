package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenError {

    INVALID_TOKEN("유효하지 않은 토큰입니다"),
    NO_REFRESH_TOKEN("Refresh Token 이 존재하지 않습니다");

    private final String message;
}
