package com.boilerplate.boilerplate.domain.post.dto;

import com.boilerplate.boilerplate.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostUserResponse {

    private Long id;
    private String username;
    private String name;

    public static PostUserResponse from(User user) {
        return PostUserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .build();
    }
}
