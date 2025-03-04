package com.boilerplate.boilerplate.config.jwt.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthenticationError {

    LOGIN_FAILURE("로그인에 실패했습니다");

    private final String message;
}
