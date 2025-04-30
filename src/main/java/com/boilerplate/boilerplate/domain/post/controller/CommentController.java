package com.boilerplate.boilerplate.domain.post.controller;

import com.boilerplate.boilerplate.domain.post.dto.CommentRepliesResponse;
import com.boilerplate.boilerplate.domain.post.dto.CommentRequest;
import com.boilerplate.boilerplate.domain.post.dto.CommentResponse;
import com.boilerplate.boilerplate.domain.post.service.CommentService;
import com.boilerplate.boilerplate.global.utils.SecurityUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentRepliesResponse>> getCommentReplies(
        @PathVariable Long commentId
    ) {
        return ResponseEntity.ok(
            commentService.getReplies(commentId)
        );
    }

    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponse> createComment(
        @PathVariable Long postId,
        @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(
            commentService.create(
                SecurityUtil.getCurrentUserId(),
                postId,
                request.getContent(),
                request.getParentCommentId()
            )
        );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
        @PathVariable Long commentId,
        @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(
            commentService.update(
                commentId,
                request.getContent()
            )
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable Long commentId
    ) {
        commentService.delete(commentId);
        return ResponseEntity.ok().build();
    }

}
