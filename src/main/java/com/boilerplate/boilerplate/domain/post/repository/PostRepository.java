package com.boilerplate.boilerplate.domain.post.repository;

import com.boilerplate.boilerplate.domain.post.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(Long userId);

    @Query("SELECT DISTINCT p FROM Post p " +
        "LEFT JOIN FETCH p.comments c " +
        "LEFT JOIN FETCH c.user " +
        "LEFT JOIN FETCH p.user " +
        "WHERE p.id = :postId")
    Optional<Post> findByIdWithComments(@Param("postId") Long postId);

    @Query("SELECT DISTINCT p FROM Post p " +
        "LEFT JOIN FETCH p.comments c " +
        "LEFT JOIN FETCH c.user " +
        "LEFT JOIN FETCH p.user")
    List<Post> findAllWithComments();

    @Query("SELECT DISTINCT p FROM Post p " +
        "LEFT JOIN FETCH p.comments c " +
        "LEFT JOIN FETCH c.user " +
        "LEFT JOIN FETCH p.user " +
        "WHERE p.user.id = :userId")
    List<Post> findByUserIdWithComments(@Param("userId") Long userId);

}
