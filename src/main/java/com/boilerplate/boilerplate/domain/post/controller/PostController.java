package com.boilerplate.boilerplate.domain.post.controller;

import com.boilerplate.boilerplate.config.jwt.JwtUserDetails;
import com.boilerplate.boilerplate.domain.post.dto.CreatePostRequest;
import com.boilerplate.boilerplate.domain.post.dto.PostResponse;
import com.boilerplate.boilerplate.domain.post.dto.UpdatePostRequest;
import com.boilerplate.boilerplate.domain.post.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
        @AuthenticationPrincipal JwtUserDetails userDetails,
        @RequestBody CreatePostRequest request
    ) {
        return ResponseEntity.ok(
            PostResponse.from(
                postService.create(
                    userDetails.getId(),
                    request.getTitle(),
                    request.getContent()
                )
            )
        );
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
        @PathVariable Long postId,
        @RequestBody UpdatePostRequest request
    ) {
        return ResponseEntity.ok(
            PostResponse.from(
                postService.update(
                    postId,
                    request.getTitle(),
                    request.getContent()
                )
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
        log.info("모든 글 조회");
        return ResponseEntity.ok(
            postService.getAllPosts().stream()
                .map(PostResponse::from)
                .toList()
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<PostResponse>> getMyPosts(
        @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        return ResponseEntity.ok(
            postService.getPostsByUserId(userDetails.getId()).stream()
                .map(PostResponse::from)
                .toList()
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        log.info("글 단건 조회: {}", postId);
        return ResponseEntity.ok(
            PostResponse.from(
                postService.getPostById(postId)
            )
        );
    }
}
