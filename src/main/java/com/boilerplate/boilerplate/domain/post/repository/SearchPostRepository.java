package com.boilerplate.boilerplate.domain.post.repository;

import com.boilerplate.boilerplate.domain.post.dto.PostSearchOptions;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchPostRepository {

    Page<Post> findPostsBySearchOptionsToPage(Pageable pageable, PostSearchOptions postSearchOptions);

    Page<Post> findUserPostsByUsernameAndSearchOptionsToPage(Pageable pageable, String username,
        PostSearchOptions postSearchOptions);

    List<Post> findPostsBySearchOptionsToCursor(Long cursor, int size, PostSearchOptions postSearchOptions);

    List<Post> findUserLikedPostsByUsernameAndSearchOptionsToCursor(Long cursor, int size, String username,
        PostSearchOptions postSearchOptions);

}
