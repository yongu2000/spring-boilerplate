package com.boilerplate.boilerplate.domain.user.exception;

import lombok.Getter;

@Getter
public enum UserError {

    ALREADY_EXIST("이미 존재하는 유저입니다");

    private final String message;

    UserError(String message) {
        this.message = message;
    }
}
