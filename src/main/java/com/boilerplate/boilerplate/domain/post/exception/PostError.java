package com.boilerplate.boilerplate.domain.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PostError {

    POST_NOT_EXIST("찾을 수 없는 게시글입니다"),
    COMMENT_NOT_EXIST("찾을 수 없는 댓글입니다: "),
    COMMENT_NO_AUTH("댓글에 대한 권한이 없습니다");


    private final String message;
}
