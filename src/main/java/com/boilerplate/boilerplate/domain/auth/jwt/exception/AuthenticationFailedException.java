package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class AuthenticationFailedException extends BusinessException {

    public AuthenticationFailedException() {
        super(AuthenticationError.AUTHENTICATION_FAILED);
    }
}
