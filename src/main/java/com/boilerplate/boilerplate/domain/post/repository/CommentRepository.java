package com.boilerplate.boilerplate.domain.post.repository;

import com.boilerplate.boilerplate.domain.post.entity.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
