package com.boilerplate.boilerplate.domain.user.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class UserDetailNotFoundException extends BusinessException {

    public UserDetailNotFoundException() {
        super(UserError.USER_DETAIL_NOT_FOUND);
    }
}
