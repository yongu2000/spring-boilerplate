package com.boilerplate.boilerplate.domain.post.repository;

import com.boilerplate.boilerplate.domain.post.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, SearchPostRepository {

    @Query("SELECT DISTINCT p FROM Post p " +
        "LEFT JOIN FETCH p.comments c " +
        "LEFT JOIN FETCH c.user " +
        "LEFT JOIN FETCH p.user " +
        "WHERE p.id = :postId")
    Optional<Post> findPostByPostId(@Param("postId") Long postId);

    @Query("SELECT DISTINCT p FROM Post p " +
        "LEFT JOIN FETCH p.comments c " +
        "LEFT JOIN FETCH c.user " +
        "LEFT JOIN FETCH p.user " +
        "WHERE p.user.id = :userId")
    List<Post> findPostsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Post p SET p.viewCounts = p.viewCounts + 1 where p.id = :postId")
    void increaseViewCounts(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes + 1 where p.id = :postId")
    void increaseLikes(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes - 1 where p.id = :postId")
    void decreaseLikes(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCounts = p.commentCounts + 1 where p.id = :postId")
    void increaseCommentCounts(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCounts = p.commentCounts - 1 where p.id = :postId")
    void decreaseCommentCounts(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.deletedAt = CURRENT_TIMESTAMP WHERE p.user.id = :userId")
    void softDeleteByUserId(Long userId);

}
