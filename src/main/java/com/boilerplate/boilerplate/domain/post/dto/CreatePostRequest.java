package com.boilerplate.boilerplate.domain.post.dto;

import lombok.Getter;

@Getter
public class CreatePostRequest {

    private String title;
    private String content;
}