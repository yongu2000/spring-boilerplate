package com.boilerplate.boilerplate.domain.post.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class NotCommentOwnerException extends BusinessException {

    public NotCommentOwnerException() {
        super(PostError.COMMENT_NOT_OWNED);
    }
}
