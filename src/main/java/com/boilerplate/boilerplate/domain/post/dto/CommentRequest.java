package com.boilerplate.boilerplate.domain.post.dto;

import lombok.Getter;

@Getter
public class CommentRequest {

    private String content;
    private Long parentCommentId;
}