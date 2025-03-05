package com.boilerplate.boilerplate.domain.post.dto;

import lombok.Getter;

@Getter
public class UpdatePostRequest {

    private String title;
    private String content;
}