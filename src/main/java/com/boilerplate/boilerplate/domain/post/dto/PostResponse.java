package com.boilerplate.boilerplate.domain.post.dto;

import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private int likes;
    private UserInfo user;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Getter
    @Builder
    public static class UserInfo {

        private Long id;
        private String username;
        private String name;

        public static UserInfo from(User user) {
            return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .build();
        }
    }

    public static PostResponse from(Post post) {
        return PostResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .likes(post.getLikes())
            .user(UserInfo.from(post.getUser()))
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build();
    }
}
