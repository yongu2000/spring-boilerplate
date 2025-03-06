package com.boilerplate.boilerplate.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.boilerplate.boilerplate.domain.post.dto.CommentResponse;
import com.boilerplate.boilerplate.domain.post.entity.Comment;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.PostError;
import com.boilerplate.boilerplate.domain.post.repository.CommentRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 서비스 CommentService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    private User mockUser;
    private Post mockPost;
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        mockUser = new User("testEmail", "testUser", "password", "testName", Role.USER);
        ReflectionTestUtils.setField(mockUser, "id", 1L);
        mockPost = new Post("Test Title", "Test Content", 0, mockUser);
        mockComment = new Comment("Test Comment", mockPost, mockUser, null);
    }

    @Test
    void 댓글_작성_성공() {
        // Given
        Long userId = 1L;
        Long postId = 1L;
        String content = "New Comment";
        when(userService.findById(userId)).thenReturn(mockUser);
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        // When
        CommentResponse comment = commentService.create(userId, postId, content, null);

        // Then
        assertThat(comment).isNotNull();
        assertThat(comment.getContent()).isEqualTo("Test Comment");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void 댓글_작성_실패_게시글없음() {
        // Given
        Long userId = 1L;
        Long postId = 999L;
        when(userService.findById(userId)).thenReturn(mockUser);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> commentService.create(userId, postId, "Comment Content", null));
        assertThat(exception.getMessage()).contains(PostError.POST_NOT_EXIST.getMessage());
    }

    @Test
    void 댓글_수정_성공() {
        // Given
        Long commentId = 1L;
        String updatedContent = "Updated Comment";
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        // When
        CommentResponse updatedComment = commentService.update(mockUser.getId(), commentId, updatedContent);

        // Then
        assertThat(updatedComment.getContent()).isEqualTo(updatedContent);
    }

    @Test
    void 댓글_수정_실패_댓글없음() {
        // Given
        Long commentId = 999L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> commentService.update(mockUser.getId(), commentId, "Updated Comment"));
        assertThat(exception.getMessage()).contains(PostError.COMMENT_NOT_EXIST.getMessage());
    }

    @Test
    void 댓글_수정_실패_권한없음() {
        // Given
        Long commentId = 1L;
        Long otherUserId = 999L; // 다른 유저 ID
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> commentService.update(otherUserId, commentId, "Updated Comment"));
        assertThat(exception.getMessage()).contains(PostError.COMMENT_NO_AUTH.getMessage());
    }


    @Test
    void 댓글_삭제_성공() {
        // Given
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
        doNothing().when(commentRepository).deleteById(commentId);

        // When
        commentService.delete(mockUser.getId(), commentId);

        // Then
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void 댓글_삭제_실패_댓글없음() {
        // Given
        Long commentId = 999L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> commentService.delete(mockUser.getId(), commentId));
        assertThat(exception.getMessage()).contains(PostError.COMMENT_NOT_EXIST.getMessage());
    }

}