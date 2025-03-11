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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            parentComment = commentRepository.findByIdWithUser(parentCommentId)
                .orElseThrow(
                    () -> new EntityNotFoundException(PostError.COMMENT_NOT_EXIST.getMessage() + parentCommentId));
        }

        Comment comment = new Comment(content, post, user, parentComment);
        post.increaseCommentCounts();
        return CommentResponse.from(commentRepository.save(comment));
    }

    @PreAuthorize("hasRole('ADMIN') or @commentSecurityChecker.isCommentOwner(#commentId)")
    public CommentResponse update(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.COMMENT_NOT_EXIST.getMessage() + commentId));
        comment.updateContent(newContent);
        return CommentResponse.from(comment);
    }

    @PreAuthorize("hasRole('ADMIN') or @commentSecurityChecker.isCommentOwner(#commentId)")
    public void delete(Long commentId) {
        Comment comment = commentRepository.findByIdWithPost(commentId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.COMMENT_NOT_EXIST.getMessage() + commentId));
        Post post = comment.getPost();
        post.decreaseCommentCounts();
        commentRepository.deleteById(commentId);
    }
}