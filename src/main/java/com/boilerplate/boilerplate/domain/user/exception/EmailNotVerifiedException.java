package com.boilerplate.boilerplate.domain.user.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class EmailNotVerifiedException extends BusinessException {

    public EmailNotVerifiedException() {
        super(UserError.EMAIL_NOT_VERIFIED);
    }
}
