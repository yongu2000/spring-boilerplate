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
    private Long likes;
    private Long commentCounts;
    private Long viewCounts;
    private PostAndCommentUserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static PostSummaryResponse from(Post post) {
        return PostSummaryResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .likes(post.getLikes())
//            .commentCounts((long) post.getComments().size())
            .commentCounts(post.getCommentCounts())
            .viewCounts(post.getViewCounts())
            .user(PostAndCommentUserResponse.from(post.getUser()))
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build();
    }
}
