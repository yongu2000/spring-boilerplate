package com.boilerplate.boilerplate.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.boilerplate.boilerplate.domain.post.dto.PostResponse;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DisplayName("게시글 서비스 PostService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
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
            .email("testEmail")
            .username("testUser")
            .password("password")
            .name("testName")
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
    void 게시글_생성_성공() {
        // Given
        Long userId = user.getId();
        System.out.println(userId);

        // When
        PostResponse createdPost = postService.create(userId, post.getTitle(), post.getContent());

        // Then
        assertThat(createdPost).isNotNull();
        assertThat(createdPost.getTitle()).isEqualTo("Test Title");
        assertThat(createdPost.getContent()).isEqualTo("Test Content");
        assertThat(createdPost.getUser().getId()).isEqualTo(userId);
    }

    @Test
    void 게시글_수정_성공() {
        // Given
        Long postId = post.getId();

        // When
        PostResponse updatedPost = postService.update(postId, "Updated Title", "Updated Content");

        // Then
        assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedPost.getContent()).isEqualTo("Updated Content");
    }

    @Test
    void 게시글_수정_실패_게시글_없음() {
        // Given
        Long postId = 99L;

        // When & Then
        assertThatThrownBy(() -> postService.update(postId, "New Title", "New Content"))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage(PostError.POST_NOT_EXIST.getMessage());
    }

//    @Test
//    void 게시글_삭제_성공() {
//        // Given
//        Long postId = post.getId();
//
//        // When
//        postService.delete(postId);
//
//        // Then
//        assertThat(postService.getAllPosts()).isEmpty();
//    }

    @Test
    void 게시글_삭제_실패_게시글_없음() {
        // Given
        Long postId = 99L;

        // When & Then
        assertThatThrownBy(() -> postService.delete(postId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage(PostError.POST_NOT_EXIST.getMessage());
    }

//    @Test
//    void 전체_글_조회_성공() {
//        // When
//        List<PostResponse> result = postService.getAllPosts();
//
//        // Then
//        assertThat(result).isNotEmpty();
//        assertThat(result.size()).isEqualTo(1);
//        assertThat(result.getFirst().getId()).isEqualTo(post.getId());
//    }

    @Test
    void 유저별_게시글_조회_성공() {
        // When
        List<PostResponse> result = postService.getPostsByUserId(user.getId());

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getId()).isEqualTo(post.getId());
    }
}