package com.boilerplate.boilerplate.domain.post.repository;

import com.boilerplate.boilerplate.domain.post.dto.PostSearchOptions;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchPostRepository {

    Page<Post> findPostsBySearchOptionsToPage(Pageable pageable, PostSearchOptions postSearchOptions);

    List<Post> findPostsBySearchOptionsToCursor(Long cursor, int size, PostSearchOptions postSearchOptions);
}
