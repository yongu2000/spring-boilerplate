package com.boilerplate.boilerplate.domain.post.repository;

import com.boilerplate.boilerplate.domain.post.entity.LikedPost;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {

    Optional<LikedPost> findByUserIdAndPostId(Long userId, Long postId);

    @Modifying
    @Query("UPDATE LikedPost lp SET lp.deletedAt = CURRENT_TIMESTAMP WHERE lp.user.id = :userId")
    void softDeleteByUserId(Long userId);

    @Modifying
    @Query("UPDATE LikedPost lp SET lp.deletedAt = CURRENT_TIMESTAMP WHERE lp.post.id = :postId")
    void softDeleteByPostId(Long postId);

}
