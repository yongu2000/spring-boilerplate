package com.boilerplate.boilerplate.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.boilerplate.boilerplate.domain.post.dto.PostResponse;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.PostError;
import com.boilerplate.boilerplate.domain.post.repository.CommentRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 서비스 PostService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("testEmail@example.com")
            .username("testUser")
            .password("password")
            .name("testName")
            .role(Role.USER)
            .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        post = Post.builder()
            .id(1L)
            .title("Test Title")
            .content("Test Content")
            .user(user)
            .build();
    }

    @Nested
    class 게시글_작성 {

        @Test
        void 게시글_작성_성공() {
            // Given
            Long userId = user.getId();
            String title = "New Title";
            String content = "New Content";
            when(userService.findById(userId)).thenReturn(user);
            when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
                Post savedPost = invocation.getArgument(0);
                ReflectionTestUtils.setField(savedPost, "id", 2L);
                return savedPost;
            });

            // When
            PostResponse createdPost = postService.create(userId, title, content);

            // Then
            assertThat(createdPost).isNotNull();
            assertThat(createdPost.getTitle()).isEqualTo(title);
            assertThat(createdPost.getContent()).isEqualTo(content);
            assertThat(createdPost.getUser().getId()).isEqualTo(userId);
            verify(postRepository, times(1)).save(any(Post.class)); // ✅ postRepository.save() 호출 검증
        }
    }

    @Nested
    class 게시글_수정 {

        @Test
        void 게시글_수정_성공() {
            // Given
            Long postId = post.getId();
            String newTitle = "Updated Title";
            String newContent = "Updated Content";
            when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

            // When
            PostResponse updatedPost = postService.update(postId, newTitle, newContent);

            // Then
            assertThat(updatedPost.getTitle()).isEqualTo(newTitle);
            assertThat(updatedPost.getContent()).isEqualTo(newContent);
            verify(postRepository, times(1)).findById(postId);
        }

        @Test
        void 게시글_수정_실패_게시글_없음() {
            // Given
            Long postId = 99L;
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> postService.update(postId, "New Title", "New Content"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PostError.POST_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class 게시글_삭제 {

        @Test
        void 게시글_삭제_성공() {
            // Given
            Long postId = post.getId();

            // When
            when(postRepository.existsById(postId)).thenReturn(true);
            postService.delete(postId);

            // Then
            verify(postRepository, times(1)).deleteById(postId); // ✅ postRepository.deleteById() 호출 검증
        }

        @Test
        void 게시글_삭제_실패_게시글_없음() {
            // Given
            Long postId = 99L;
            when(postRepository.existsById(postId)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> postService.delete(postId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PostError.POST_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class 게시글_조회 {

        @Test
        void 유저별_게시글_조회_성공() {
            // Given
            Long userId = user.getId();
            when(postRepository.findPostsByUserId(userId)).thenReturn(List.of(post));

            // When
            List<PostResponse> result = postService.getPostsByUserId(userId);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.size()).isEqualTo(1);
            assertThat(result.get(0).getId()).isEqualTo(post.getId());
        }
    }
}