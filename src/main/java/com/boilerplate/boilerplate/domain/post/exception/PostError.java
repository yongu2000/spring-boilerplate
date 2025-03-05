package com.boilerplate.boilerplate.domain.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PostError {

    POST_NOT_EXIST("존재하지 않는 게시글입니다");

    private final String message;
}
