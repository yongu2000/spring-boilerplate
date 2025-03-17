package com.boilerplate.boilerplate.domain.post.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class NotPostOwnerException extends BusinessException {

    public NotPostOwnerException() {
        super(PostError.POST_NOT_OWNED);
    }
}
