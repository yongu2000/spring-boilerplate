package com.boilerplate.boilerplate.domain.post.service;

import com.boilerplate.boilerplate.domain.post.dto.CommentResponse;
import com.boilerplate.boilerplate.domain.post.entity.Comment;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.PostError;
import com.boilerplate.boilerplate.domain.post.repository.CommentRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public CommentResponse create(Long userId, Long postId, String content, Long parentCommentId) {
        User user = userService.findById(userId);
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.POST_NOT_EXIST.getMessage() + postId));

        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(
                    () -> new EntityNotFoundException(PostError.COMMENT_NOT_EXIST.getMessage() + parentCommentId));
        }

        Comment comment = new Comment(content, post, user, parentComment);
        return CommentResponse.from(commentRepository.save(comment));
    }

    public CommentResponse update(Long userId, Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.COMMENT_NOT_EXIST.getMessage() + commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException(PostError.COMMENT_NO_AUTH.getMessage());
        }
        comment.updateContent(newContent);
        return CommentResponse.from(comment);
    }

    public void delete(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.COMMENT_NOT_EXIST.getMessage() + commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException(PostError.COMMENT_NO_AUTH.getMessage());
        }
        commentRepository.deleteById(commentId);
    }

    public List<CommentResponse> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId).stream()
            .filter(comment -> comment.getParentComment() == null)  // 최상위 댓글만 가져오기
            .map(CommentResponse::from)
            .toList();
    }
}