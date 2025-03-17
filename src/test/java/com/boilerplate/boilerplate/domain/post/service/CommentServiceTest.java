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
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
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

    @Mock
    private User mockUser;
    @Mock
    private Post mockPost;
    @Mock
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        when(mockComment.getUser()).thenReturn(mockUser);
    }

    @Nested
    class 댓글_작성 {

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
        void 대댓글_작성_성공() {
            // Given
            Long userId = 1L;
            Long postId = 1L;
            Long parentCommentId = 1L;
            String content = "Reply Comment";

            when(userService.findById(userId)).thenReturn(mockUser);
            when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
            when(commentRepository.findByIdWithUser(parentCommentId)).thenReturn(Optional.of(mockComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

            // When
            CommentResponse response = commentService.create(userId, postId, content, parentCommentId);

            // Then
            assertThat(response).isNotNull();
            verify(commentRepository).save(any(Comment.class));
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
                () -> commentService.create(userId, postId, "Test Comment", null));
            assertThat(exception.getMessage()).contains(PostError.POST_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class 댓글_수정 {

        @Test
        void 댓글_수정_성공() {
            // Given
            Long userId = 1L;
            Long commentId = 1L;
            String updatedContent = "Updated Comment";
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

            // When
            CommentResponse updatedComment = commentService.update(commentId, updatedContent);

            // Then
            assertThat(updatedComment.getContent()).isEqualTo(updatedContent);
            verify(commentRepository).findById(commentId);
        }

        @Test
        void 댓글_수정_실패_댓글없음() {
            // Given
            Long commentId = 999L;
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.update(commentId, "Updated Comment"));
            assertThat(exception.getMessage()).contains(PostError.COMMENT_NOT_FOUND.getMessage());
        }

    }

    @Nested
    class 댓글_삭제 {

        @Test
        void 댓글_삭제_성공() {
            // Given
            Long commentId = 1L;

            when(commentRepository.findByIdWithPost(commentId)).thenReturn(Optional.of(mockComment));
            when(mockComment.getPost()).thenReturn(mockPost);  // ✅ post.getCommentCounts() 호출을 대비
            doNothing().when(commentRepository).deleteById(commentId);

            // When
            commentService.delete(commentId);

            // Then
            verify(commentRepository, times(1)).deleteById(commentId);
            verify(mockPost, times(1)).decreaseCommentCounts(); // ✅ 댓글 개수 감소 확인
        }

        @Test
        void 댓글_삭제_실패_댓글없음() {
            // Given
            Long commentId = 999L;
            when(commentRepository.findByIdWithPost(commentId)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.delete(commentId));
            assertThat(exception.getMessage()).contains(PostError.COMMENT_NOT_FOUND.getMessage());
        }
    }

}