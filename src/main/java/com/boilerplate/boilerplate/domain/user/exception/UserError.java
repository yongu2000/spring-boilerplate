package com.boilerplate.boilerplate.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserError {

    ALREADY_EXIST("이미 존재하는 유저입니다"),
    NO_SUCH_USER("존재하지 않는 유저입니다");
    
    private final String message;
}
