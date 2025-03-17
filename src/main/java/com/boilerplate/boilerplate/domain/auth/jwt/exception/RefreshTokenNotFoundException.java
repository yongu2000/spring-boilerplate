package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class RefreshTokenNotFoundException extends BusinessException {

    public RefreshTokenNotFoundException() {
        super(TokenError.REFRESH_TOKEN_NOT_FOUND);
    }

}
