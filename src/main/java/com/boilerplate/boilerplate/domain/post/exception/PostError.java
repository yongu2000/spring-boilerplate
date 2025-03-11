package com.boilerplate.boilerplate.domain.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PostError {

    POST_NOT_EXIST("찾을 수 없는 게시글입니다"),
    COMMENT_NOT_EXIST("찾을 수 없는 댓글입니다: "),
    LIKED_POST_NOT_EXIST("좋아요 한 게시글이 없습니다"),
    LIKED_POST_ALREADY_EXISTS("이미 좋아요 한 게시글입니다");


    private final String message;
}
