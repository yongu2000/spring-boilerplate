package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class InvalidRefreshTokenException extends BusinessException {

    public InvalidRefreshTokenException() {
        super(TokenError.INVALID_TOKEN);
    }
}
