package com.boilerplate.boilerplate.domain.user.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(UserError.USER_NOT_FOUND);
    }
}
