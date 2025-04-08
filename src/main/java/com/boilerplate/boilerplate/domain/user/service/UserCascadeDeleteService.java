package com.boilerplate.boilerplate.domain.user.service;

import com.boilerplate.boilerplate.domain.post.repository.CommentRepository;
import com.boilerplate.boilerplate.domain.post.repository.LikedPostRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCascadeDeleteService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikedPostRepository likedPostRepository;

    public void deleteByUser(User user) {
        Long userId = user.getId();
        likedPostRepository.softDeleteByUserId(userId);
        commentRepository.softDeleteByUserId(userId);
        postRepository.softDeleteByUserId(userId);
    }
}
