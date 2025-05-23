package com.boilerplate.boilerplate.domain.post.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class CommentNotFoundException extends BusinessException {

    public CommentNotFoundException() {
        super(PostError.COMMENT_NOT_FOUND);
    }
}
