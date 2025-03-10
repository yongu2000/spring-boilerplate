package com.boilerplate.boilerplate.domain.post.service;

import com.boilerplate.boilerplate.domain.global.dto.CursorResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostSummaryResponse;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.PostError;
import com.boilerplate.boilerplate.domain.post.repository.CommentRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
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

    public PostResponse update(Long postId, String newTitle, String newContent) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.POST_NOT_EXIST.getMessage()));

        post.update(newTitle, newContent);

        return PostResponse.from(post);
    }

    public void delete(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(PostError.POST_NOT_EXIST.getMessage());
        }
        postRepository.deleteById(postId);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getAllPostsByPage(Pageable pageable) {
        Page<Post> posts = postRepository.findAllPostSummariesByPage(pageable);
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        // 현재 조회 중인 게시글들에 대한 댓글 개수 조회
        List<Object[]> commentCounts = commentRepository.findCommentCountsByPostIds(postIds);
        Map<Long, Long> commentCountMap = commentCounts.stream()
            .collect(Collectors.toMap(
                obj -> (Long) obj[0],  // postId
                obj -> (Long) obj[1]   // commentCount
            ));

        return posts.map(post -> {
            long commentCount = commentCountMap.getOrDefault(post.getId(), 0L);
            return PostSummaryResponse.from(post, commentCount);
        });
    }

    @Transactional(readOnly = true)
    public CursorResponse<PostSummaryResponse> getAllPostsByCursor(Long cursor, int size) {
        // 커서 기반 조회
        List<Post> posts = postRepository.findAllPostsByCursor(cursor, size + 1);

        // hasNext 확인을 위해 size + 1개를 조회했으므로, 실제 응답에는 size개만 포함
        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts = posts.subList(0, size);
        }

        // 댓글 수 조회 및 매핑
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<Long, Long> commentCountMap = commentRepository.findCommentCountsByPostIds(postIds).stream()
            .collect(Collectors.toMap(
                obj -> (Long) obj[0],
                obj -> (Long) obj[1],
                (v1, v2) -> v1,
                HashMap::new
            ));

        List<PostSummaryResponse> responses = posts.stream()
            .map(post -> PostSummaryResponse.from(post, commentCountMap.getOrDefault(post.getId(), 0L)))
            .collect(Collectors.toList());

        // 다음 커서는 마지막 게시글의 ID
        Long nextCursor = hasNext && !posts.isEmpty() ? posts.getLast().getId() : null;

        return new CursorResponse<>(responses, nextCursor, hasNext);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUserId(Long userId) {
        return postRepository.findByUserIdWithComments(userId).stream()
            .map(PostResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        return PostResponse.from(postRepository.findByIdWithComments(postId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.POST_NOT_EXIST.getMessage())));
    }
}
