package com.boilerplate.boilerplate.domain.post.dto;

import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private int likes;
    private UserInfo user;
    private List<CommentResponse> comments; // 이 필드가 있는지 확인
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Getter
    @Builder
    private static class UserInfo {

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
            .comments(post.getComments().stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList())) // comments 매핑 확인
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build();
    }
}
