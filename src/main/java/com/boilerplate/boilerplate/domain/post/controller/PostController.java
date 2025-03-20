package com.boilerplate.boilerplate.domain.post.controller;

import com.boilerplate.boilerplate.domain.post.dto.CreatePostRequest;
import com.boilerplate.boilerplate.domain.post.dto.PostLikeStatusResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostSearchOptions;
import com.boilerplate.boilerplate.domain.post.dto.PostSummaryResponse;
import com.boilerplate.boilerplate.domain.post.dto.UpdatePostRequest;
import com.boilerplate.boilerplate.domain.post.service.PostService;
import com.boilerplate.boilerplate.global.dto.CursorResponse;
import com.boilerplate.boilerplate.global.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
        @Valid @RequestBody CreatePostRequest request
    ) {
        return ResponseEntity.ok(
            postService.create(
                SecurityUtil.getCurrentUserId(),
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


    @GetMapping("/list")
    public ResponseEntity<Page<PostSummaryResponse>> getAllPostsWithSearchOptionsByPage(
        Pageable pageable,
        @ModelAttribute PostSearchOptions postSearchOptions
    ) {
        return ResponseEntity.ok(
            postService.getAllPostsWithSearchOptionsToPage(pageable, postSearchOptions)
        );
    }

    @GetMapping("/grid")
    public ResponseEntity<CursorResponse<PostSummaryResponse>> getAllPostsByCursor(
        @RequestParam(required = false) Long cursor,
        @RequestParam(defaultValue = "10") int size,
        @ModelAttribute PostSearchOptions postSearchOptions
    ) {
        return ResponseEntity.ok(
            postService.getAllPostsWithSearchOptionsToCursor(cursor, size, postSearchOptions)
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        log.info("getPost");
        return ResponseEntity.ok(
            postService.getPostById(postId)
        );
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        postService.like(SecurityUtil.getCurrentUserId(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/dislike")
    public ResponseEntity<Void> dislikePost(@PathVariable Long postId) {
        postService.dislike(SecurityUtil.getCurrentUserId(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/like/status")
    public ResponseEntity<PostLikeStatusResponse> getLikeStatus(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getLikeStatus(SecurityUtil.getCurrentUserId(), postId));
    }
}
