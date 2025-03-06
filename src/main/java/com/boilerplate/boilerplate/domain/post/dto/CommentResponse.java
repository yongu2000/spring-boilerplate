package com.boilerplate.boilerplate.domain.post.dto;

import com.boilerplate.boilerplate.domain.post.entity.Comment;
import com.boilerplate.boilerplate.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private UserInfo user;
    private Long parentCommentId;
    private List<CommentResponse> replies;
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

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .user(UserInfo.from(comment.getUser()))
            .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
            .replies(comment.getReplies().stream()
                .map(CommentResponse::from)
                .toList())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build();
    }
}
