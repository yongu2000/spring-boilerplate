package com.boilerplate.boilerplate.domain.post.validation;

import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.PostError;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.global.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("postSecurityChecker")
@RequiredArgsConstructor
public class PostSecurityChecker {

    private final PostRepository postRepository;

    public boolean isPostOwner(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException(PostError.POST_NOT_EXIST.getMessage()));
        return post.getUser().getId().equals(SecurityUtil.getCurrentUserId());

    }
}
