package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class InvalidTokenTypeException extends BusinessException {

    public InvalidTokenTypeException() {
        super(TokenError.INVALID_TOKEN_TYPE);
    }
}
