package com.boilerplate.boilerplate.domain.post.controller;

import com.boilerplate.boilerplate.config.jwt.JwtUserDetails;
import com.boilerplate.boilerplate.domain.post.dto.CommentRequest;
import com.boilerplate.boilerplate.domain.post.dto.CommentResponse;
import com.boilerplate.boilerplate.domain.post.service.CommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
        @AuthenticationPrincipal JwtUserDetails userDetails,
        @PathVariable Long postId,
        @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(
            commentService.create(
                userDetails.getId(),
                postId,
                request.getContent(),
                request.getParentCommentId()
            )
        );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
        @AuthenticationPrincipal JwtUserDetails userPrincipal,
        @PathVariable Long commentId,
        @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(
            commentService.update(
                userPrincipal.getId(),
                commentId,
                request.getContent()
            )
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @AuthenticationPrincipal JwtUserDetails userPrincipal,
        @PathVariable Long commentId
    ) {
        commentService.delete(userPrincipal.getId(), commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }
}
