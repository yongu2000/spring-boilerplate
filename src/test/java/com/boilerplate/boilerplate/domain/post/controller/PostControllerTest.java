package com.boilerplate.boilerplate.domain.post.controller;

import static com.boilerplate.boilerplate.utils.TestReflectionUtil.setId;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.post.dto.CreatePostRequest;
import com.boilerplate.boilerplate.domain.post.dto.PostAndCommentUserResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostLikeStatusResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostSearchOptions;
import com.boilerplate.boilerplate.domain.post.dto.PostSummaryResponse;
import com.boilerplate.boilerplate.domain.post.dto.UpdatePostRequest;
import com.boilerplate.boilerplate.domain.post.exception.DuplicateLikedPostException;
import com.boilerplate.boilerplate.domain.post.exception.NotPostOwnerException;
import com.boilerplate.boilerplate.domain.post.exception.PostError;
import com.boilerplate.boilerplate.domain.post.exception.PostNotFoundException;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSearchType;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSortBy;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSortDirection;
import com.boilerplate.boilerplate.domain.post.service.PostService;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.global.dto.CursorResponse;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@WebMvcTest(PostController.class)
@DisplayName("게시글 Controller")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @MockitoBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private PostResponse testPostResponse;
    private PostSummaryResponse testPostSummaryResponse;
    private PostSearchOptions searchOptions;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .email("test@example.com")
            .username("test")
            .name("test")
            .build();
        setId(testUser, 1L);

        PostAndCommentUserResponse user = PostAndCommentUserResponse.from(testUser);
        testPostResponse = PostResponse.builder()
            .id(1L)
            .likes(0L)
            .commentCounts(0L)
            .viewCounts(0L)
            .title("Test Title")
            .content("Test Content")
            .user(user)
            .comments(null)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

        testPostSummaryResponse = PostSummaryResponse.builder()
            .id(1L)
            .title("Test Title")
            .user(user)
            .build();

        searchOptions = new PostSearchOptions();
        searchOptions.setSearchType(PostSearchType.TITLE);
        searchOptions.setSearchKeyword("Test");
        searchOptions.setStartDate(LocalDate.now());
        searchOptions.setEndDate(LocalDate.now());
        searchOptions.setSortBy(PostSortBy.DATE);
        searchOptions.setSortDirection(PostSortDirection.DESC);

        setUpAuthentication();
    }

    void setUpAuthentication() {
        CustomUserDetails userDetails = new CustomUserDetails(testUser); // 생성자 필요
        System.out.println("userDetails.user = " + userDetails.getId());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 게시글_생성_성공() throws Exception {
        // given
        CreatePostRequest request = new CreatePostRequest("Test Title", "Test Content");
        given(postService.create(any(), any(), any())).willReturn(testPostResponse);

        // when & then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Test Title"))
            .andExpect(jsonPath("$.content").value("Test Content"))
            .andDo(document("post-create",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 생성 API")
                    .description("새로운 게시글을 생성합니다")
                    .requestFields(
                        fieldWithPath("title").description("게시글 제목"),
                        fieldWithPath("content").description("게시글 내용")
                    )
                    .responseFields(
                        fieldWithPath("id").description("게시글 ID"),
                        fieldWithPath("title").description("게시글 제목"),
                        fieldWithPath("content").description("게시글 내용"),
                        fieldWithPath("likes").description("좋아요 수"),
                        fieldWithPath("commentCounts").description("댓글 수"),
                        fieldWithPath("viewCounts").description("조회수"),
                        fieldWithPath("user.id").description("작성자 ID"),
                        fieldWithPath("user.username").description("작성자 이름"),
                        fieldWithPath("user.name").description("작성자 표시 이름"),
                        fieldWithPath("comments").description("댓글 목록").optional(),
                        fieldWithPath("createdAt").description("게시글 생성 시간"),
                        fieldWithPath("modifiedAt").description("게시글 수정 시간")
                    )
                    .requestSchema(schema("CreatePostRequest"))
                    .responseSchema(schema("PostResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_수정_성공() throws Exception {
        // given
        UpdatePostRequest request = new UpdatePostRequest("Updated Title", "Updated Content");
        given(postService.update(any(), any(), any())).willReturn(testPostResponse);

        // when & then
        mockMvc.perform(put("/api/posts/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Test Title"))
            .andExpect(jsonPath("$.content").value("Test Content"))
            .andDo(document("post-update",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 수정 API")
                    .description("기존 게시글을 수정합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .requestFields(
                        fieldWithPath("title").description("수정할 게시글 제목"),
                        fieldWithPath("content").description("수정할 게시글 내용")
                    )
                    .responseFields(
                        fieldWithPath("id").description("게시글 ID"),
                        fieldWithPath("title").description("게시글 제목"),
                        fieldWithPath("content").description("게시글 내용"),
                        fieldWithPath("likes").description("좋아요 수"),
                        fieldWithPath("commentCounts").description("댓글 수"),
                        fieldWithPath("viewCounts").description("조회수"),
                        fieldWithPath("user.id").description("작성자 ID"),
                        fieldWithPath("user.username").description("작성자 이름"),
                        fieldWithPath("user.name").description("작성자 표시 이름"),
                        fieldWithPath("comments").description("댓글 목록").optional(),
                        fieldWithPath("createdAt").description("게시글 생성 시간"),
                        fieldWithPath("modifiedAt").description("게시글 수정 시간")
                    )
                    .requestSchema(schema("UpdatePostRequest"))
                    .responseSchema(schema("PostResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_삭제_성공() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/posts/{postId}", 1L))
            .andExpect(status().isOk())
            .andDo(document("post-delete",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 삭제 API")
                    .description("게시글을 삭제합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .responseFields()
                    .build()
                )
            ));
    }

    @Test
    void 게시글_검색_옵션_조회_성공() throws Exception {
        // given
        Page<PostSummaryResponse> page = new PageImpl<>(List.of(testPostSummaryResponse));
        given(postService.getAllPostsWithSearchOptionsToPage(any(), any())).willReturn(page);

        // when & then
        mockMvc.perform(get("/api/posts/list")
                .param("page", "0")
                .param("size", "10")
                .param("searchType", "TITLE")
                .param("searchKeyword", "Test")
                .param("sortBy", "DATE")
                .param("sortDirection", "DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[0].title").value("Test Title"))
            .andDo(document("post-search",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 검색 API")
                    .description("검색 옵션을 사용하여 게시글을 조회합니다")
                    .queryParameters(
                        parameterWithName("searchType").description("검색 유형 (TITLE, CONTENT 등)").optional(),
                        parameterWithName("searchKeyword").description("검색어").optional(),
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("sortBy").description("정렬 기준 (DATE, LIKES, COMMENTS)").optional(),
                        parameterWithName("sortDirection").description("정렬 방향 (ASC, DESC)").optional()
                    )
                    .responseFields(
                        fieldWithPath("content[].id").description("게시글 ID"),
                        fieldWithPath("content[].title").description("게시글 제목"),
                        fieldWithPath("content[].content").description("게시글 내용"),
                        fieldWithPath("content[].likes").description("좋아요 수"),
                        fieldWithPath("content[].commentCounts").description("댓글 수"),
                        fieldWithPath("content[].viewCounts").description("조회수"),
                        fieldWithPath("content[].user.id").description("작성자 ID"),
                        fieldWithPath("content[].user.username").description("작성자 이름"),
                        fieldWithPath("content[].user.name").description("작성자 표시 이름"),
                        fieldWithPath("content[].createdAt").description("게시글 생성 시간"),
                        fieldWithPath("content[].modifiedAt").description("게시글 수정 시간"),
                        fieldWithPath("pageable").description("페이지 요청 정보"),
                        fieldWithPath("totalElements").description("전체 데이터 수"),
                        fieldWithPath("totalPages").description("전체 페이지 수"),
                        fieldWithPath("last").description("마지막 페이지 여부"),
                        fieldWithPath("size").description("페이지 크기"),
                        fieldWithPath("number").description("현재 페이지 번호"),
                        fieldWithPath("sort.empty").description("정렬 정보 없음 여부"),
                        fieldWithPath("sort.sorted").description("정렬 여부"),
                        fieldWithPath("sort.unsorted").description("미정렬 여부"),
                        fieldWithPath("numberOfElements").description("현재 페이지의 데이터 수"),
                        fieldWithPath("first").description("첫 페이지 여부"),
                        fieldWithPath("empty").description("데이터 없음 여부")
                    )
                    .responseSchema(schema("Page<PostSummaryResponse>"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_좋아요_성공() throws Exception {
        // when & then
        mockMvc.perform(post("/api/posts/{postId}/like", 1L))
            .andExpect(status().isOk())
            .andDo(document("post-like",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 좋아요 API")
                    .description("게시글에 좋아요를 추가합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .responseFields()
                    .build()
                )
            ));
    }

    @Test
    void 게시글_좋아요_취소_성공() throws Exception {
        // when & then
        mockMvc.perform(post("/api/posts/{postId}/dislike", 1L))
            .andExpect(status().isOk())
            .andDo(document("post-dislike",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 좋아요 취소 API")
                    .description("게시글의 좋아요를 취소합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .responseFields()
                    .build()
                )
            ));
    }

    @Test
    void 게시글_좋아요_상태_조회_성공() throws Exception {
        // given
        PostLikeStatusResponse response = new PostLikeStatusResponse(true);
        given(postService.getLikeStatus(any(), any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/posts/{postId}/like/status", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.liked").value(true))
            .andDo(document("post-like-status",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 좋아요 상태 조회 API")
                    .description("게시글의 좋아요 상태를 조회합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .responseFields(
                        fieldWithPath("liked").description("좋아요 여부")
                    )
                    .responseSchema(schema("PostLikeStatusResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_수정_실패_권한_없음() throws Exception {
        // given
        UpdatePostRequest request = new UpdatePostRequest("Updated Title", "Updated Content");
        given(postService.update(any(), any(), any())).willThrow(new NotPostOwnerException());

        // when & then
        mockMvc.perform(put("/api/posts/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(PostError.POST_NOT_OWNED.getMessage()))
            .andExpect(jsonPath("$.code").value(PostError.POST_NOT_OWNED.name()))
            .andExpect(jsonPath("$.status").value(PostError.POST_NOT_OWNED.getStatus().value()))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.details").isMap())
            .andDo(document("post-update-failure-not-owner",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 수정 실패 API")
                    .description("권한이 없는 경우 게시글 수정에 실패합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .requestFields(
                        fieldWithPath("title").description("수정할 게시글 제목"),
                        fieldWithPath("content").description("수정할 게시글 내용")
                    )
                    .responseFields(
                        fieldWithPath("message").description("에러 메시지"),
                        fieldWithPath("status").description("HTTP 상태 코드"),
                        fieldWithPath("code").description("에러 코드"),
                        fieldWithPath("timestamp").description("에러 발생 시각"),
                        fieldWithPath("details").description("추가 에러 정보"),
                        fieldWithPath("details.message").description("추가 에러 메세지")
                    )
                    .requestSchema(schema("UpdatePostRequest"))
                    .responseSchema(schema("ErrorResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_삭제_실패_존재하지_않음() throws Exception {
        // given
        doThrow(new PostNotFoundException()).when(postService).delete(any());

        // when & then
        mockMvc.perform(delete("/api/posts/{postId}", 1L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(PostError.POST_NOT_FOUND.getMessage()))
            .andExpect(jsonPath("$.code").value(PostError.POST_NOT_FOUND.name()))
            .andExpect(jsonPath("$.status").value(PostError.POST_NOT_FOUND.getStatus().value()))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.details").isMap())
            .andDo(document("post-delete-failure-not-found",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 삭제 실패 API")
                    .description("존재하지 않는 게시글 삭제에 실패합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .responseFields(
                        fieldWithPath("message").description("에러 메시지"),
                        fieldWithPath("status").description("HTTP 상태 코드"),
                        fieldWithPath("code").description("에러 코드"),
                        fieldWithPath("timestamp").description("에러 발생 시각"),
                        fieldWithPath("details").description("추가 에러 정보"),
                        fieldWithPath("details.message").description("추가 에러 메세지")
                    )
                    .responseSchema(schema("ErrorResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_좋아요_실패_중복() throws Exception {
        // given
        doThrow(new DuplicateLikedPostException()).when(postService).like(any(), any());

        // when & then
        mockMvc.perform(post("/api/posts/{postId}/like", 1L))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(PostError.DUPLICATE_LIKED_POST.getMessage()))
            .andExpect(jsonPath("$.code").value(PostError.DUPLICATE_LIKED_POST.name()))
            .andExpect(jsonPath("$.status").value(PostError.DUPLICATE_LIKED_POST.getStatus().value()))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.details").isMap())
            .andDo(document("post-like-failure-duplicate",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 좋아요 실패 API")
                    .description("이미 좋아요한 게시글에 대해 좋아요에 실패합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .responseFields(
                        fieldWithPath("message").description("에러 메시지"),
                        fieldWithPath("status").description("HTTP 상태 코드"),
                        fieldWithPath("code").description("에러 코드"),
                        fieldWithPath("timestamp").description("에러 발생 시각"),
                        fieldWithPath("details").description("추가 에러 정보"),
                        fieldWithPath("details.message").description("추가 에러 메세지")
                    )
                    .responseSchema(schema("ErrorResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_커서_기반_조회_성공() throws Exception {
        // given
        CursorResponse<PostSummaryResponse> response = new CursorResponse<>(List.of(testPostSummaryResponse), 2L, true);
        given(postService.getAllPostsWithSearchOptionsToCursor(anyLong(), anyInt(), any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/posts/grid")
                .param("cursor", "1")
                .param("size", "10")
                .param("searchType", "TITLE")
                .param("searchKeyword", "Test")
                .param("sortBy", "DATE")
                .param("sortDirection", "DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].id").value(1L))
            .andExpect(jsonPath("$.items[0].title").value("Test Title"))
            .andExpect(jsonPath("$.hasNext").value(true))
            .andExpect(jsonPath("$.nextCursor").value(2L))
            .andDo(document("post-cursor-search",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 커서 기반 조회 API")
                    .description("커서 기반 페이지네이션을 사용하여 게시글을 조회합니다")
                    .queryParameters(
                        parameterWithName("cursor").description("커서 ID (첫 페이지 조회시 생략)").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("searchType").description("검색 유형 (TITLE, CONTENT 등)").optional(),
                        parameterWithName("searchKeyword").description("검색어").optional(),
                        parameterWithName("sortBy").description("정렬 기준 (DATE, LIKES, COMMENTS)").optional(),
                        parameterWithName("sortDirection").description("정렬 방향 (ASC, DESC)").optional()
                    )
                    .responseFields(
                        fieldWithPath("items[].id").description("게시글 ID"),
                        fieldWithPath("items[].title").description("게시글 제목"),
                        fieldWithPath("items[].content").description("게시글 내용"),
                        fieldWithPath("items[].likes").description("좋아요 수"),
                        fieldWithPath("items[].commentCounts").description("댓글 수"),
                        fieldWithPath("items[].viewCounts").description("조회수"),
                        fieldWithPath("items[].user.id").description("작성자 ID"),
                        fieldWithPath("items[].user.username").description("작성자 이름"),
                        fieldWithPath("items[].user.name").description("작성자 표시 이름"),
                        fieldWithPath("items[].createdAt").description("게시글 생성 시간"),
                        fieldWithPath("items[].modifiedAt").description("게시글 수정 시간"),
                        fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                        fieldWithPath("nextCursor").description("다음 페이지 커서 ID")
                    )
                    .responseSchema(schema("CursorResponse<PostSummaryResponse>"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_커서_기반_조회_마지막_페이지() throws Exception {
        // given
        CursorResponse<PostSummaryResponse> response = new CursorResponse<>(
            List.of(testPostSummaryResponse), null, false);
        given(postService.getAllPostsWithSearchOptionsToCursor(anyLong(), anyInt(), any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/posts/grid")
                .param("cursor", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].id").value(1L))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.nextCursor").isEmpty())
            .andDo(document("post-cursor-search-last-page",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 커서 기반 조회 API (마지막 페이지)")
                    .description("마지막 페이지의 게시글을 조회합니다")
                    .queryParameters(
                        parameterWithName("cursor").description("커서 ID"),
                        parameterWithName("size").description("페이지 크기")
                    )
                    .responseFields(
                        fieldWithPath("items[].id").description("게시글 ID"),
                        fieldWithPath("items[].title").description("게시글 제목"),
                        fieldWithPath("items[].content").description("게시글 내용"),
                        fieldWithPath("items[].likes").description("좋아요 수"),
                        fieldWithPath("items[].commentCounts").description("댓글 수"),
                        fieldWithPath("items[].viewCounts").description("조회수"),
                        fieldWithPath("items[].user.id").description("작성자 ID"),
                        fieldWithPath("items[].user.username").description("작성자 이름"),
                        fieldWithPath("items[].user.name").description("작성자 표시 이름"),
                        fieldWithPath("items[].createdAt").description("게시글 생성 시간"),
                        fieldWithPath("items[].modifiedAt").description("게시글 수정 시간"),
                        fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                        fieldWithPath("nextCursor").description("다음 페이지 커서 ID (마지막 페이지인 경우 null)")
                    )
                    .responseSchema(schema("CursorResponse<PostSummaryResponse>"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_상세_조회_성공() throws Exception {
        // given
        given(postService.getPostById(any())).willReturn(testPostResponse);

        // when & then
        mockMvc.perform(get("/api/posts/{postId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Test Title"))
            .andExpect(jsonPath("$.content").value("Test Content"))
            .andExpect(jsonPath("$.likes").value(0L))
            .andExpect(jsonPath("$.commentCounts").value(0L))
            .andExpect(jsonPath("$.viewCounts").value(0L))
            .andExpect(jsonPath("$.user.id").value(1L))
            .andExpect(jsonPath("$.user.username").value("test"))
            .andExpect(jsonPath("$.user.name").value("test"))
            .andDo(document("post-detail",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 상세 조회 API")
                    .description("게시글의 상세 정보를 조회합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .responseFields(
                        fieldWithPath("id").description("게시글 ID"),
                        fieldWithPath("title").description("게시글 제목"),
                        fieldWithPath("content").description("게시글 내용"),
                        fieldWithPath("likes").description("좋아요 수"),
                        fieldWithPath("commentCounts").description("댓글 수"),
                        fieldWithPath("viewCounts").description("조회수"),
                        fieldWithPath("user.id").description("작성자 ID"),
                        fieldWithPath("user.username").description("작성자 이름"),
                        fieldWithPath("user.name").description("작성자 표시 이름"),
                        fieldWithPath("comments").description("댓글 목록").optional(),
                        fieldWithPath("createdAt").description("게시글 생성 시간"),
                        fieldWithPath("modifiedAt").description("게시글 수정 시간")
                    )
                    .responseSchema(schema("PostResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 게시글_상세_조회_실패_존재하지_않음() throws Exception {
        // given
        given(postService.getPostById(any())).willThrow(new PostNotFoundException());

        // when & then
        mockMvc.perform(get("/api/posts/{postId}", 1L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(PostError.POST_NOT_FOUND.getMessage()))
            .andExpect(jsonPath("$.code").value(PostError.POST_NOT_FOUND.name()))
            .andExpect(jsonPath("$.status").value(PostError.POST_NOT_FOUND.getStatus().value()))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.details").isMap())
            .andDo(document("post-detail-failure-not-found",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("게시글 상세 조회 실패 API")
                    .description("존재하지 않는 게시글 조회에 실패합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .responseFields(
                        fieldWithPath("message").description("에러 메시지"),
                        fieldWithPath("status").description("HTTP 상태 코드"),
                        fieldWithPath("code").description("에러 코드"),
                        fieldWithPath("timestamp").description("에러 발생 시각"),
                        fieldWithPath("details").description("추가 에러 정보"),
                        fieldWithPath("details.message").description("추가 에러 메세지")
                    )
                    .responseSchema(schema("ErrorResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 사용자_게시글_목록_조회_성공() throws Exception {
        // given
        Page<PostSummaryResponse> page = new PageImpl<>(List.of(testPostSummaryResponse));
        given(postService.getUserPostsByUsernameWithSearchOptionsToPage(any(), any(), any())).willReturn(page);

        // when & then
        mockMvc.perform(get("/api/posts/{username}/list", "test")
                .param("page", "0")
                .param("size", "10")
                .param("searchType", "TITLE")
                .param("searchKeyword", "Test")
                .param("sortBy", "DATE")
                .param("sortDirection", "DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[0].title").value("Test Title"))
            .andDo(document("user-posts-list",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("사용자 게시글 목록 조회 API")
                    .description("특정 사용자의 게시글 목록을 조회합니다")
                    .pathParameters(
                        parameterWithName("username").description("사용자 이름")
                    )
                    .queryParameters(
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("searchType").description("검색 유형 (TITLE, CONTENT 등)").optional(),
                        parameterWithName("searchKeyword").description("검색어").optional(),
                        parameterWithName("sortBy").description("정렬 기준 (DATE, LIKES, COMMENTS)").optional(),
                        parameterWithName("sortDirection").description("정렬 방향 (ASC, DESC)").optional()
                    )
                    .responseFields(
                        fieldWithPath("content[].id").description("게시글 ID"),
                        fieldWithPath("content[].title").description("게시글 제목"),
                        fieldWithPath("content[].content").description("게시글 내용"),
                        fieldWithPath("content[].likes").description("좋아요 수"),
                        fieldWithPath("content[].commentCounts").description("댓글 수"),
                        fieldWithPath("content[].viewCounts").description("조회수"),
                        fieldWithPath("content[].user.id").description("작성자 ID"),
                        fieldWithPath("content[].user.username").description("작성자 이름"),
                        fieldWithPath("content[].user.name").description("작성자 표시 이름"),
                        fieldWithPath("content[].createdAt").description("게시글 생성 시간"),
                        fieldWithPath("content[].modifiedAt").description("게시글 수정 시간"),
                        fieldWithPath("pageable").description("페이지 요청 정보"),
                        fieldWithPath("totalElements").description("전체 데이터 수"),
                        fieldWithPath("totalPages").description("전체 페이지 수"),
                        fieldWithPath("last").description("마지막 페이지 여부"),
                        fieldWithPath("size").description("페이지 크기"),
                        fieldWithPath("number").description("현재 페이지 번호"),
                        fieldWithPath("sort.empty").description("정렬 정보 없음 여부"),
                        fieldWithPath("sort.sorted").description("정렬 여부"),
                        fieldWithPath("sort.unsorted").description("미정렬 여부"),
                        fieldWithPath("numberOfElements").description("현재 페이지의 데이터 수"),
                        fieldWithPath("first").description("첫 페이지 여부"),
                        fieldWithPath("empty").description("데이터 없음 여부")
                    )
                    .responseSchema(schema("Page<PostSummaryResponse>"))
                    .build()
                )
            ));
    }

    @Test
    void 사용자_좋아요_게시글_목록_조회_성공() throws Exception {
        // given
        CursorResponse<PostSummaryResponse> response = new CursorResponse<>(List.of(testPostSummaryResponse), 2L, true);
        given(postService.getUserLikedPostByUsernameWithSearchOptionsToCursor(anyLong(), anyInt(), any(),
            any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/posts/{username}/like/grid", "test")
                .param("cursor", "1")
                .param("size", "10")
                .param("searchType", "TITLE")
                .param("searchKeyword", "Test")
                .param("sortBy", "DATE")
                .param("sortDirection", "DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].id").value(1L))
            .andExpect(jsonPath("$.items[0].title").value("Test Title"))
            .andExpect(jsonPath("$.hasNext").value(true))
            .andExpect(jsonPath("$.nextCursor").value(2L))
            .andDo(document("user-liked-posts-grid",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("사용자 좋아요 게시글 목록 조회 API")
                    .description("특정 사용자가 좋아요한 게시글 목록을 커서 기반으로 조회합니다")
                    .pathParameters(
                        parameterWithName("username").description("사용자 이름")
                    )
                    .queryParameters(
                        parameterWithName("cursor").description("커서 ID (첫 페이지 조회시 생략)").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("searchType").description("검색 유형 (TITLE, CONTENT 등)").optional(),
                        parameterWithName("searchKeyword").description("검색어").optional(),
                        parameterWithName("sortBy").description("정렬 기준 (DATE, LIKES, COMMENTS)").optional(),
                        parameterWithName("sortDirection").description("정렬 방향 (ASC, DESC)").optional()
                    )
                    .responseFields(
                        fieldWithPath("items[].id").description("게시글 ID"),
                        fieldWithPath("items[].title").description("게시글 제목"),
                        fieldWithPath("items[].content").description("게시글 내용"),
                        fieldWithPath("items[].likes").description("좋아요 수"),
                        fieldWithPath("items[].commentCounts").description("댓글 수"),
                        fieldWithPath("items[].viewCounts").description("조회수"),
                        fieldWithPath("items[].user.id").description("작성자 ID"),
                        fieldWithPath("items[].user.username").description("작성자 이름"),
                        fieldWithPath("items[].user.name").description("작성자 표시 이름"),
                        fieldWithPath("items[].createdAt").description("게시글 생성 시간"),
                        fieldWithPath("items[].modifiedAt").description("게시글 수정 시간"),
                        fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                        fieldWithPath("nextCursor").description("다음 페이지 커서 ID")
                    )
                    .responseSchema(schema("CursorResponse<PostSummaryResponse>"))
                    .build()
                )
            ));
    }

    @Test
    void 사용자_좋아요_게시글_목록_조회_마지막_페이지() throws Exception {
        // given
        CursorResponse<PostSummaryResponse> response = new CursorResponse<>(List.of(testPostSummaryResponse), null,
            false);
        given(postService.getUserLikedPostByUsernameWithSearchOptionsToCursor(anyLong(), anyInt(), any(),
            any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/posts/{username}/like/grid", "test")
                .param("cursor", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].id").value(1L))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.nextCursor").isEmpty())
            .andDo(document("user-liked-posts-grid-last-page",
                resource(ResourceSnippetParameters.builder()
                    .tag("게시글")
                    .summary("사용자 좋아요 게시글 목록 조회 API (마지막 페이지)")
                    .description("특정 사용자가 좋아요한 게시글의 마지막 페이지를 조회합니다")
                    .pathParameters(
                        parameterWithName("username").description("사용자 이름")
                    )
                    .queryParameters(
                        parameterWithName("cursor").description("커서 ID"),
                        parameterWithName("size").description("페이지 크기")
                    )
                    .responseFields(
                        fieldWithPath("items[].id").description("게시글 ID"),
                        fieldWithPath("items[].title").description("게시글 제목"),
                        fieldWithPath("items[].content").description("게시글 내용"),
                        fieldWithPath("items[].likes").description("좋아요 수"),
                        fieldWithPath("items[].commentCounts").description("댓글 수"),
                        fieldWithPath("items[].viewCounts").description("조회수"),
                        fieldWithPath("items[].user.id").description("작성자 ID"),
                        fieldWithPath("items[].user.username").description("작성자 이름"),
                        fieldWithPath("items[].user.name").description("작성자 표시 이름"),
                        fieldWithPath("items[].createdAt").description("게시글 생성 시간"),
                        fieldWithPath("items[].modifiedAt").description("게시글 수정 시간"),
                        fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                        fieldWithPath("nextCursor").description("다음 페이지 커서 ID (마지막 페이지인 경우 null)")
                    )
                    .responseSchema(schema("CursorResponse<PostSummaryResponse>"))
                    .build()
                )
            ));
    }
} 