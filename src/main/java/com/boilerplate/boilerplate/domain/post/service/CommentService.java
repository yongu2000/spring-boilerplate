package com.boilerplate.boilerplate.domain.post.service;

import com.boilerplate.boilerplate.domain.post.dto.CommentRepliesResponse;
import com.boilerplate.boilerplate.domain.post.dto.CommentResponse;
import com.boilerplate.boilerplate.domain.post.entity.Comment;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.CommentNotFoundException;
import com.boilerplate.boilerplate.domain.post.repository.CommentRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import java.util.List;
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
            .orElseThrow(CommentNotFoundException::new);

        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findByIdWithUser(parentCommentId)
                .orElseThrow(
                    CommentNotFoundException::new);
        }

        Comment comment = new Comment(content, post, user, parentComment);
        postRepository.increaseCommentCounts(post.getId());
        return CommentResponse.from(commentRepository.save(comment));
    }

    @PreAuthorize("hasRole('ADMIN') or @commentSecurityChecker.isCommentOwner(#commentId)")
    public CommentResponse update(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(CommentNotFoundException::new);
        comment.updateContent(newContent);
        return CommentResponse.from(comment);
    }

    @PreAuthorize("hasRole('ADMIN') or @commentSecurityChecker.isCommentOwner(#commentId)")
    public void delete(Long commentId) {
        Comment comment = commentRepository.findByIdWithPost(commentId)
            .orElseThrow(CommentNotFoundException::new);
        Post post = comment.getPost();
        postRepository.decreaseCommentCounts(post.getId());
        commentRepository.deleteById(commentId);
    }

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostId(postId).stream()
            .map(CommentResponse::from)
            .toList();
    }

    public List<CommentRepliesResponse> getReplies(Long commentId) {
        return commentRepository.findRepliesById(commentId).stream()
            .map(CommentRepliesResponse::from)
            .toList();
    }
}