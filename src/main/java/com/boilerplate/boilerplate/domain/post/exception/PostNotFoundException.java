package com.boilerplate.boilerplate.domain.post.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class PostNotFoundException extends BusinessException {

    public PostNotFoundException() {
        super(PostError.POST_NOT_FOUND);
    }
}
