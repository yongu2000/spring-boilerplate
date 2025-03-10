package com.boilerplate.boilerplate.domain.post.controller;

import com.boilerplate.boilerplate.config.jwt.JwtUserDetails;
import com.boilerplate.boilerplate.domain.post.dto.CreatePostRequest;
import com.boilerplate.boilerplate.domain.post.dto.PostResponse;
import com.boilerplate.boilerplate.domain.post.dto.UpdatePostRequest;
import com.boilerplate.boilerplate.domain.post.service.PostService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
        @AuthenticationPrincipal JwtUserDetails userDetails,
        @Valid @RequestBody CreatePostRequest request
    ) {
        return ResponseEntity.ok(
            postService.create(
                userDetails.getId(),
                request.getTitle(),
                request.getContent()
            )
        );
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
        @PathVariable Long postId,
        @Valid @RequestBody UpdatePostRequest request
    ) {
        return ResponseEntity.ok(
            postService.update(
                postId,
                request.getTitle(),
                request.getContent()
            )
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(
            postService.getAllPosts()
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<PostResponse>> getMyPosts(
        @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        return ResponseEntity.ok(
            postService.getPostsByUserId(userDetails.getId())
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(
            postService.getPostById(postId)
        );
    }
}
