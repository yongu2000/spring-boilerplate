package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class InvalidJsonRequestException extends BusinessException {

    public InvalidJsonRequestException() {
        super(AuthenticationError.INVALID_JSON_REQUEST);
    }
}

