package com.boilerplate.boilerplate.domain.user.exception;

import com.boilerplate.boilerplate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserError implements ErrorCode {

    DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 유저입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다"),
    INVALID_PASSWORD(HttpStatus.NOT_FOUND, "비밀번호가 일치하지 않습니다"),
    USER_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "유효한 인증 정보가 없습니다");

    private final HttpStatus status;
    private final String message;

}
