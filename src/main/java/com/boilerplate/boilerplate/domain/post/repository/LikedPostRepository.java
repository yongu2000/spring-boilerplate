package com.boilerplate.boilerplate.domain.post.repository;

import com.boilerplate.boilerplate.domain.post.entity.LikedPost;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {

    Optional<LikedPost> findByUserIdAndPostId(Long userId, Long postId);

}
