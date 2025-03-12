package com.boilerplate.boilerplate.domain.auth.jwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RefreshTokenNotFoundException extends RuntimeException {

    public RefreshTokenNotFoundException() {
        super(TokenError.NO_REFRESH_TOKEN.getMessage());
    }

}
