package com.boilerplate.boilerplate.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserError {

    ALREADY_EXIST("이미 존재하는 유저입니다");

    private final String message;
}
