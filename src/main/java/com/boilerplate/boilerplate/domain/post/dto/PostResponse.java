package com.boilerplate.boilerplate.domain.post.dto;

import com.boilerplate.boilerplate.domain.post.entity.Post;
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
    private PostUserResponse user;
    private List<CommentResponse> comments; // 이 필드가 있는지 확인
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static PostResponse from(Post post) {
        return PostResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .likes(post.getLikes())
            .user(PostUserResponse.from(post.getUser()))
            .comments(post.getComments().stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList())) // comments 매핑 확인
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build();
    }
}
