package com.boilerplate.boilerplate.domain.post.dto;

import com.boilerplate.boilerplate.domain.post.entity.Post;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostSummaryResponse {

    private Long id;
    private String title;
    private String content;
    private int likes;
    private PostUserResponse user;
    private long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static PostSummaryResponse from(Post post, Long commentCount) {
        return PostSummaryResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .likes(post.getLikes())
            .user(PostUserResponse.from(post.getUser()))
            .commentCount(commentCount)
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build();
    }
}
