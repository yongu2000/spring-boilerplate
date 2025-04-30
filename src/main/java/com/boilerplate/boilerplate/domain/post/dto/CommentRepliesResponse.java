package com.boilerplate.boilerplate.domain.post.dto;

import com.boilerplate.boilerplate.domain.post.entity.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentRepliesResponse {

    private Long id;
    private String content;
    private PostAndCommentUserResponse user;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CommentRepliesResponse from(Comment comment) {
        return CommentRepliesResponse.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .user(PostAndCommentUserResponse.from(comment.getUser()))
            .parentCommentId(
                comment.getParentComment() != null ? comment.getParentComment().getId() : null)
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build();
    }
}
