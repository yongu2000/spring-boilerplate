package com.boilerplate.boilerplate.domain.post.service;

import static com.boilerplate.boilerplate.utils.TestReflectionUtil.setId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.boilerplate.boilerplate.domain.post.dto.CommentResponse;
import com.boilerplate.boilerplate.domain.post.entity.Comment;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.CommentNotFoundException;
import com.boilerplate.boilerplate.domain.post.repository.CommentRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 서비스 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private Comment parentComment;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .email("test@example.com")
            .build();
        setId(testUser, 1L);
        testPost = Post.builder()
            .id(1L)
            .title("Test Title")
            .content("Test Content")
            .user(testUser)
            .build();
        setId(testPost, 1L);

        parentComment = Comment.builder()
            .content("Parent Comment")
            .post(testPost)
            .user(testUser)
            .build();
        setId(parentComment, 1L);

        testComment = Comment.builder()
            .content("Test Comment")
            .post(testPost)
            .user(testUser)
            .parentComment(parentComment)
            .build();
        setId(testComment, 2L);

    }

    @Test
    void 댓글_생성() {
        // given
        given(userService.findById(1L)).willReturn(testUser);
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        given(commentRepository.findByIdWithUser(1L)).willReturn(Optional.of(parentComment));
        given(commentRepository.save(any(Comment.class))).willReturn(testComment);

        // when
        CommentResponse result = commentService.create(1L, 1L, "Test Comment", 1L);

        // then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getContent()).isEqualTo("Test Comment");
        assertThat(result.getUser().getId()).isEqualTo(1L);
        assertThat(result.getParentCommentId()).isEqualTo(1L);
        assertThat(testPost.getCommentCounts()).isEqualTo(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void 부모_댓글_없는_댓글_생성() {
        // given
        given(userService.findById(1L)).willReturn(testUser);
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        given(commentRepository.save(any(Comment.class))).willReturn(parentComment);

        // when
        CommentResponse result = commentService.create(1L, 1L, "Parent Comment", null);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("Parent Comment");
        assertThat(result.getUser().getId()).isEqualTo(1L);
        assertThat(result.getParentCommentId()).isNull();
        assertThat(testPost.getCommentCounts()).isEqualTo(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void 존재하지_않는_게시글_댓글_작성_예외_발생() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.create(1L, 1L, "Test Comment", null))
            .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 존재하지_않는_부모_댓글_답글_예외_발생() {
        // given
        given(userService.findById(1L)).willReturn(testUser);
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        given(commentRepository.findByIdWithUser(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.create(1L, 1L, "Test Comment", 1L))
            .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 댓글_수정() {
        // given
        given(commentRepository.findById(2L)).willReturn(Optional.of(testComment));

        // when
        CommentResponse result = commentService.update(2L, "Updated Comment");

        // then
        assertThat(result.getContent()).isEqualTo("Updated Comment");
    }

    @Test
    void 존재하지_않는_댓글_수정_예외_발생() {
        // given
        given(commentRepository.findById(2L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.update(2L, "Updated Comment"))
            .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 댓글_삭제() {
        // given
        given(commentRepository.findByIdWithPost(2L)).willReturn(Optional.of(testComment));
        testPost.increaseCommentCounts();

        // when
        commentService.delete(2L);

        // then
        assertThat(testPost.getCommentCounts()).isEqualTo(0L);
        verify(commentRepository).deleteById(2L);
    }

    @Test
    void 존재하지_않는_댓글_삭제_예외_발생() {
        // given
        given(commentRepository.findByIdWithPost(2L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.delete(2L))
            .isInstanceOf(CommentNotFoundException.class);
    }
} 