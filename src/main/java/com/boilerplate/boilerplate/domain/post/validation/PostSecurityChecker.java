package com.boilerplate.boilerplate.domain.post.validation;

import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.NotPostOwnerException;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("postSecurityChecker")
@RequiredArgsConstructor
public class PostSecurityChecker {

    private final PostRepository postRepository;

    public boolean isPostOwner(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(NotPostOwnerException::new);
        return post.getUser().getId().equals(SecurityUtil.getCurrentUserId());

    }
}
