package com.boilerplate.boilerplate.domain.user.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class PasswordResetTokenNotMatchException extends BusinessException {

    public PasswordResetTokenNotMatchException() {
        super(UserError.PASSWORD_RESET_TOKEN_NOT_MATCH);
    }
}
