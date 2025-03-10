package com.boilerplate.boilerplate.domain.post.repository;

import com.boilerplate.boilerplate.domain.post.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DISTINCT p FROM Post p " +
        "LEFT JOIN FETCH p.comments c " +
        "LEFT JOIN FETCH c.user " +
        "LEFT JOIN FETCH p.user " +
        "WHERE p.id = :postId")
    Optional<Post> findByIdWithComments(@Param("postId") Long postId);

    @Query(value = "SELECT p FROM Post p " +
        "LEFT JOIN FETCH p.user u " +
        "ORDER BY p.id DESC",
        countQuery = "SELECT COUNT(p) FROM Post p"
    )
    Page<Post> findAllPostSummariesByPage(Pageable pageable);

    @Query("SELECT p FROM Post p " +
        "LEFT JOIN FETCH p.user " +
        "WHERE (:cursor IS NULL OR p.id < :cursor) " +
        "ORDER BY p.id DESC " +
        "LIMIT :limit")
    List<Post> findAllPostsByCursor(@Param("cursor") Long cursor, @Param("limit") int limit);


    @Query("SELECT DISTINCT p FROM Post p " +
        "LEFT JOIN FETCH p.comments c " +
        "LEFT JOIN FETCH c.user " +
        "LEFT JOIN FETCH p.user " +
        "WHERE p.user.id = :userId")
    List<Post> findByUserIdWithComments(@Param("userId") Long userId);

}
