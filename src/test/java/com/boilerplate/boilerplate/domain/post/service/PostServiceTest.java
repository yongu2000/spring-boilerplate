package com.boilerplate.boilerplate.domain.post.service;

import static com.boilerplate.boilerplate.utils.TestReflectionUtil.setId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.boilerplate.boilerplate.domain.post.dto.PostLikeStatusResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostSearchOptions;
import com.boilerplate.boilerplate.domain.post.dto.PostSummaryResponse;
import com.boilerplate.boilerplate.domain.post.entity.LikedPost;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.exception.DuplicateLikedPostException;
import com.boilerplate.boilerplate.domain.post.exception.PostNotFoundException;
import com.boilerplate.boilerplate.domain.post.repository.LikedPostRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSearchType;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSortBy;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSortDirection;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.dto.CursorResponse;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 서비스 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikedPostRepository likedPostRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;
    private PostSearchOptions searchOptions;

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

        searchOptions = new PostSearchOptions();
        searchOptions.setSearchType(PostSearchType.TITLE);
        searchOptions.setSearchKeyword("Test");
        searchOptions.setStartDate(LocalDate.now());
        searchOptions.setEndDate(LocalDate.now());
        searchOptions.setSortBy(PostSortBy.DATE);
        searchOptions.setSortDirection(PostSortDirection.DESC);
    }

    @Test
    void 게시글_생성() {
        // given
        given(userService.findById(1L)).willReturn(testUser);
        given(postRepository.save(any(Post.class))).willReturn(testPost);

        // when
        PostResponse result = postService.create(1L, "Test Title", "Test Content");

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(result.getContent()).isEqualTo("Test Content");
        assertThat(result.getUser().getId()).isEqualTo(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void 게시글_수정() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));

        // when
        PostResponse result = postService.update(1L, "Updated Title", "Updated Content");

        // then
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getContent()).isEqualTo("Updated Content");
    }

    @Test
    void 존재하지_않는_게시글_수정_예외_발생() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.update(1L, "Updated Title", "Updated Content"))
            .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void 게시글_삭제() {
        // given
        given(postRepository.existsById(1L)).willReturn(true);

        // when
        postService.delete(1L);

        // then
        verify(postRepository).deleteById(1L);
    }

    @Test
    void 존재하지_않는_게시글_삭제_예외_발생() {
        // given
        given(postRepository.existsById(1L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> postService.delete(1L))
            .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void 게시글_검색_옵션_조회() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(testPost));
        given(postRepository.findPostsBySearchOptionsToPage(pageable, searchOptions)).willReturn(postPage);

        // when
        Page<PostSummaryResponse> result = postService.getAllPostsWithSearchOptionsToPage(pageable, searchOptions);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(1L);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Test Title");
    }

    @Test
    void 게시글_커서_기반_조회() {
        // given
        List<Post> posts = List.of(testPost);
        given(postRepository.findPostsBySearchOptionsToCursor(0L, 11, searchOptions)).willReturn(posts);

        // when
        CursorResponse<PostSummaryResponse> result = postService.getAllPostsWithSearchOptionsToCursor(0L, 10,
            searchOptions);

        // then
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().getId()).isEqualTo(1L);
        assertThat(result.items().getFirst().getTitle()).isEqualTo("Test Title");
    }

    @Test
    void 게시글_좋아요() {
        // given
        given(userService.findById(1L)).willReturn(testUser);
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        given(likedPostRepository.findByUserIdAndPostId(1L, 1L)).willReturn(Optional.empty());

        // when
        postService.like(1L, 1L);

        // then
        assertThat(testPost.getLikes()).isEqualTo(1L);
        verify(likedPostRepository).save(any(LikedPost.class));
    }

    @Test
    void 이미_좋아요한_게시글_좋아요_예외_발생() {
        // given
        given(likedPostRepository.findByUserIdAndPostId(1L, 1L))
            .willReturn(Optional.of(LikedPost.builder().build()));

        // when & then
        assertThatThrownBy(() -> postService.like(1L, 1L))
            .isInstanceOf(DuplicateLikedPostException.class);
    }

    @Test
    void 게시글_좋아요_취소() {
        // given
        LikedPost likedPost = LikedPost.builder()
            .user(testUser)
            .post(testPost)
            .build();
        given(likedPostRepository.findByUserIdAndPostId(1L, 1L)).willReturn(Optional.of(likedPost));
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        testPost.increaseLikes();

        // when
        postService.dislike(1L, 1L);

        // then
        assertThat(testPost.getLikes()).isEqualTo(0L);
        verify(likedPostRepository).delete(likedPost);
    }

    @Test
    void 게시글_좋아요_상태_조회() {
        // given
        given(likedPostRepository.findByUserIdAndPostId(1L, 1L))
            .willReturn(Optional.of(LikedPost.builder().build()));

        // when
        PostLikeStatusResponse result = postService.getLikeStatus(1L, 1L);

        // then
        assertThat(result.isLiked()).isTrue();
    }

    @Test
    void 게시글_단건_조회_조회수_증가() {
        // given
        given(postRepository.findPostByPostId(1L)).willReturn(Optional.of(testPost));

        // when
        PostResponse result = postService.getPostById(1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(testPost.getViewCounts()).isEqualTo(1L); // 조회수 증가 확인
    }

    @Test
    void 유저_게시글_페이지_조회() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(testPost));
        given(postRepository.findUserPostsByUsernameAndSearchOptionsToPage(pageable, "testUser", searchOptions))
            .willReturn(postPage);

        // when
        Page<PostSummaryResponse> result = postService.getUserPostsByUsernameWithSearchOptionsToPage(pageable,
            "testUser", searchOptions);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(1L);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Test Title");
    }

    @Test
    void 유저_좋아요한_게시글_커서_조회() {
        // given
        List<Post> posts = List.of(testPost);
        given(postRepository.findUserLikedPostsByUsernameAndSearchOptionsToCursor(0L, 11, "testUser", searchOptions))
            .willReturn(posts);

        // when
        CursorResponse<PostSummaryResponse> result = postService.getUserLikedPostByUsernameWithSearchOptionsToCursor(0L,
            10, "testUser", searchOptions);

        // then
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().getId()).isEqualTo(1L);
        assertThat(result.items().getFirst().getTitle()).isEqualTo("Test Title");
    }

    @Test
    void 유저ID로_게시글_리스트_조회() {
        // given
        given(postRepository.findPostsByUserId(1L)).willReturn(List.of(testPost));

        // when
        List<PostResponse> result = postService.getPostsByUserId(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getTitle()).isEqualTo("Test Title");
    }
} 