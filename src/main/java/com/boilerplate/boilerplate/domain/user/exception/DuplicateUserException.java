package com.boilerplate.boilerplate.domain.user.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class DuplicateUserException extends BusinessException {

    public DuplicateUserException() {
        super(UserError.DUPLICATE_USER);
    }
}
