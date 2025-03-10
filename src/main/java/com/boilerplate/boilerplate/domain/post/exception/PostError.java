package com.boilerplate.boilerplate.domain.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PostError {

    POST_NOT_EXIST("찾을 수 없는 게시글입니다"),
    COMMENT_NOT_EXIST("찾을 수 없는 댓글입니다: ");


    private final String message;
}
