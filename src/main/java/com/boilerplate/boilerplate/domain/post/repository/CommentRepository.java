package com.boilerplate.boilerplate.domain.post.repository;

import com.boilerplate.boilerplate.domain.post.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
        "LEFT JOIN FETCH c.user " +
        "WHERE c.id = :commentId")
    Optional<Comment> findByIdWithUser(@Param("commentId") Long commentId);

    @Query("SELECT c FROM Comment c " +
        "LEFT JOIN FETCH c.post " +
        "WHERE c.id = :commentId")
    Optional<Comment> findByIdWithPost(@Param("commentId") Long commentId);

    @Modifying
    @Query("UPDATE Comment c SET c.deletedAt = CURRENT_TIMESTAMP WHERE c.user.id = :userId")
    void softDeleteByUserId(Long userId);

    @Query("SELECT r FROM Comment r " +
        "JOIN FETCH r.user " +
        "WHERE r.parentComment.id = :commentId")
    List<Comment> findRepliesById(@Param("commentId") Long commentId);

    @Query("SELECT c FROM Comment c " +
        "JOIN FETCH c.user " +
        "LEFT JOIN FETCH c.post " +
        "WHERE c.post.id = :postId AND c.parentComment IS NULL")
    List<Comment> findByPostId(@Param("postId") Long postId);
}
