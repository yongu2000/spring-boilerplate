package com.boilerplate.boilerplate.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.PostError;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .username("testuser")
            .password("password")
            .role(Role.USER)
            .build();
        post = Post.builder()
            .title("Test Title")
            .content("Test Content")
            .user(user)
            .build();
        userRepository.save(user);
        postRepository.save(post);
    }

    @Test
    void createPost_성공() {
        // given
        Long userId = user.getId();
        System.out.println(userId);

        // when
        Post createdPost = postService.create(userId, post.getTitle(), post.getContent());

        // then
        assertThat(createdPost).isNotNull();
        assertThat(createdPost.getTitle()).isEqualTo("Test Title");
        assertThat(createdPost.getContent()).isEqualTo("Test Content");
        assertThat(createdPost.getUser()).isEqualTo(user);
    }

    @Test
    void updatePost_성공() {
        // given
        Long postId = post.getId();

        // when
        Post updatedPost = postService.update(postId, "Updated Title", "Updated Content");

        // then
        assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedPost.getContent()).isEqualTo("Updated Content");
    }

    @Test
    void updatePost_실패_존재하지않는_게시글() {
        // given
        Long postId = 99L;

        // when & then
        assertThatThrownBy(() -> postService.update(postId, "New Title", "New Content"))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage(PostError.POST_NOT_EXIST.getMessage());
    }

    @Test
    void deletePost_성공() {
        // given
        Long postId = post.getId();

        // when
        postService.delete(postId);

        // then
        assertThat(postService.getAllPosts()).isEmpty();
    }

    @Test
    void deletePost_실패_존재하지않는_게시글() {
        // given
        Long postId = 99L;

        // when & then
        assertThatThrownBy(() -> postService.delete(postId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage(PostError.POST_NOT_EXIST.getMessage());
    }

    @Test
    void getAllPosts_성공() {
        // when
        List<Post> result = postService.getAllPosts();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst()).isEqualTo(post);
    }

    @Test
    void getPostsByUserId_성공() {
        // when
        List<Post> result = postService.getPostsByUserId(user.getId());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst()).isEqualTo(post);
    }
}