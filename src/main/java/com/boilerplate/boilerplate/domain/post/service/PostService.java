package com.boilerplate.boilerplate.domain.post.service;

import com.boilerplate.boilerplate.domain.post.dto.PostLikeStatusResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostSearchOptions;
import com.boilerplate.boilerplate.domain.post.dto.PostSummaryResponse;
import com.boilerplate.boilerplate.domain.post.entity.LikedPost;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.DuplicateLikedPostException;
import com.boilerplate.boilerplate.domain.post.exception.PostNotFoundException;
import com.boilerplate.boilerplate.domain.post.repository.LikedPostRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.dto.CursorResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final LikedPostRepository likedPostRepository;
    private final UserService userService;

    public PostResponse create(Long userId, String title, String content) {
        User user = userService.findById(userId);

        Post post = Post.builder()
            .title(title)
            .content(content)
            .user(user)
            .build();

        return PostResponse.from(postRepository.save(post));
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurityChecker.isPostOwner(#postId)")
    public PostResponse update(Long postId, String newTitle, String newContent) {
        Post post = postRepository.findById(postId)
            .orElseThrow(PostNotFoundException::new);

        post.update(newTitle, newContent);

        return PostResponse.from(post);
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurityChecker.isPostOwner(#postId)")
    public void delete(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }
        likedPostRepository.softDeleteByPostId(postId); // 이부분 많아지면 PostCascadeDeleteService 만들기
        postRepository.deleteById(postId);

    }

    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getAllPostsWithSearchOptionsToPage(Pageable pageable,
        PostSearchOptions searchOptions) {
        return postRepository
            .findPostsBySearchOptionsToPage(pageable, searchOptions)
            .map(PostSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public CursorResponse<PostSummaryResponse> getAllPostsWithSearchOptionsToCursor(Long cursor,
        int size,
        PostSearchOptions searchOptions) {
        // 커서 기반 조회
        List<Post> posts = postRepository.findPostsBySearchOptionsToCursor(cursor, size + 1,
            searchOptions);

        // hasNext 확인을 위해 size + 1개를 조회했으므로, 실제 응답에는 size개만 포함
        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts = posts.subList(0, size);
        }

        // 다음 커서는 마지막 게시글의 ID
        Long nextCursor = hasNext && !posts.isEmpty() ? posts.getLast().getId() : null;
        return new CursorResponse<>(posts.stream().map(PostSummaryResponse::from).toList(),
            nextCursor, hasNext);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getUserPostsByUsernameWithSearchOptionsToPage(
        Pageable pageable, String username,
        PostSearchOptions searchOptions) {
        return postRepository.findUserPostsByUsernameAndSearchOptionsToPage(pageable, username,
                searchOptions)
            .map(PostSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public CursorResponse<PostSummaryResponse> getUserLikedPostByUsernameWithSearchOptionsToCursor(
        Long cursor,
        int size,
        String username,
        PostSearchOptions searchOptions) {
        // 커서 기반 조회
        List<Post> posts = postRepository.findUserLikedPostsByUsernameAndSearchOptionsToCursor(
            cursor, size + 1,
            username,
            searchOptions);

        // hasNext 확인을 위해 size + 1개를 조회했으므로, 실제 응답에는 size개만 포함
        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts = posts.subList(0, size);
        }

        // 다음 커서는 마지막 게시글의 ID
        Long nextCursor = hasNext && !posts.isEmpty() ? posts.getLast().getId() : null;
        return new CursorResponse<>(posts.stream().map(PostSummaryResponse::from).toList(),
            nextCursor, hasNext);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUserId(Long userId) {
        return postRepository.findPostsByUserId(userId).stream()
            .map(PostResponse::from)
            .toList();
    }

    @Transactional
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findPostByPostId(postId)
            .orElseThrow(PostNotFoundException::new);
        postRepository.increaseViewCounts(post.getId());
        return PostResponse.from(post);
    }

    @Transactional
    public void like(Long userId, Long postId) {
        if (likedPostRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            throw new DuplicateLikedPostException();
        }
        User user = userService.findById(userId);
        Post post = postRepository.findById(postId)
            .orElseThrow(PostNotFoundException::new);
        postRepository.increaseLikes(post.getId());
        likedPostRepository.save(LikedPost.builder()
            .user(user)
            .post(post)
            .build());
    }

    @Transactional
    public void dislike(Long userId, Long postId) {
        LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, postId)
            .orElseThrow(PostNotFoundException::new);
        Post post = postRepository.findById(postId)
            .orElseThrow(PostNotFoundException::new);
        postRepository.decreaseLikes(post.getId());
        likedPostRepository.delete(likedPost);
    }

    @Transactional(readOnly = true)
    public PostLikeStatusResponse getLikeStatus(Long userId, Long postId) {
        boolean liked = likedPostRepository.findByUserIdAndPostId(userId, postId).isPresent();
        return new PostLikeStatusResponse(liked);
    }
}
