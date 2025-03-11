package com.boilerplate.boilerplate.domain.post.validation;

import com.boilerplate.boilerplate.domain.post.entity.Comment;
import com.boilerplate.boilerplate.domain.post.exception.PostError;
import com.boilerplate.boilerplate.domain.post.repository.CommentRepository;
import com.boilerplate.boilerplate.global.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("commentSecurityChecker")
@RequiredArgsConstructor
public class CommentSecurityChecker {

    private final CommentRepository commentRepository;

    public boolean isCommentOwner(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.POST_NOT_EXIST.getMessage()));
        return comment.getUser().getId().equals(SecurityUtil.getCurrentUserId());
    }
}
