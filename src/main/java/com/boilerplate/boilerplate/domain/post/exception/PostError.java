package com.boilerplate.boilerplate.domain.post.exception;

import com.boilerplate.boilerplate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum PostError implements ErrorCode {

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 게시글입니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 댓글입니다: "),
    LIKED_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 한 게시글이 없습니다"),
    DUPLICATE_LIKED_POST(HttpStatus.CONFLICT, "이미 좋아요 한 게시글입니다"),

    COMMENT_NOT_OWNED(HttpStatus.FORBIDDEN, "댓글에 대한 권한이 없습니다"),
    POST_NOT_OWNED(HttpStatus.FORBIDDEN, "게시글에 대한 권한이 없습니다");


    private final HttpStatus status;
    private final String message;
}
