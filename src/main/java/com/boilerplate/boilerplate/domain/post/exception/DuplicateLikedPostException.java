package com.boilerplate.boilerplate.domain.post.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class DuplicateLikedPostException extends BusinessException {

    public DuplicateLikedPostException() {
        super(PostError.DUPLICATE_LIKED_POST);
    }
}
