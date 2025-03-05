package com.boilerplate.boilerplate.domain.post.service;

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

    public Comment create(Long userId, Long postId, String content, Long parentCommentId) {
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
        return commentRepository.save(comment);
    }

    public Comment update(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.COMMENT_NOT_EXIST.getMessage() + commentId));

        comment.updateContent(newContent);
        return comment;
    }

    public void delete(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(PostError.COMMENT_NOT_EXIST.getMessage() + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    // 특정 게시글의 모든 댓글 가져오기
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }
}
