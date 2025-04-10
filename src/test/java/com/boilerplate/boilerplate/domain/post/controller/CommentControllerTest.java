package com.boilerplate.boilerplate.domain.post.controller;

import static com.boilerplate.boilerplate.utils.TestReflectionUtil.setId;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.post.dto.CommentRequest;
import com.boilerplate.boilerplate.domain.post.dto.CommentResponse;
import com.boilerplate.boilerplate.domain.post.dto.PostAndCommentUserResponse;
import com.boilerplate.boilerplate.domain.post.exception.CommentNotFoundException;
import com.boilerplate.boilerplate.domain.post.exception.NotCommentOwnerException;
import com.boilerplate.boilerplate.domain.post.exception.PostError;
import com.boilerplate.boilerplate.domain.post.service.CommentService;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@WebMvcTest(CommentController.class)
@DisplayName("댓글 Controller")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private CommentResponse testCommentResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .email("test@example.com")
            .username("test")
            .name("test")
            .role(Role.USER)
            .build();
        setId(testUser, 1L);

        PostAndCommentUserResponse user = PostAndCommentUserResponse.from(testUser);
        setUpAuthentication();
        testCommentResponse = CommentResponse.builder()
            .id(1L)
            .content("Test Comment")
            .user(user)
            .parentCommentId(null)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();
    }

    void setUpAuthentication() {
        CustomUserDetails userDetails = new CustomUserDetails(testUser); // 생성자 필요
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 댓글_생성_성공() throws Exception {
        // given
        CommentRequest request = new CommentRequest("Test Comment", null);
        given(commentService.create(any(), any(), any(), any())).willReturn(testCommentResponse);

        // when & then
        mockMvc.perform(post("/api/posts/{postId}/comments", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.content").value("Test Comment"))
            .andExpect(jsonPath("$.parentCommentId").isEmpty())
            .andDo(document("comment-create",
                resource(ResourceSnippetParameters.builder()
                    .tag("댓글")
                    .summary("댓글 생성 API")
                    .description("새로운 댓글을 생성합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .requestFields(
                        fieldWithPath("content").description("댓글 내용"),
                        fieldWithPath("parentCommentId").description("부모 댓글 ID (답글인 경우)")
                    )
                    .responseFields(
                        fieldWithPath("id").description("댓글 ID"),
                        fieldWithPath("content").description("댓글 내용"),
                        fieldWithPath("user.id").description("작성자 ID"),
                        fieldWithPath("user.username").description("작성자 이름"),
                        fieldWithPath("user.name").description("작성자 표시 이름"),
                        fieldWithPath("parentCommentId").description("부모 댓글 ID"),
                        fieldWithPath("replies").description("답글 목록").optional(),
                        fieldWithPath("createdAt").description("댓글 생성 시간"),
                        fieldWithPath("modifiedAt").description("댓글 수정 시간")
                    )
                    .requestSchema(schema("CommentRequest"))
                    .responseSchema(schema("CommentResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 답글_생성_성공() throws Exception {
        // given
        CommentRequest request = new CommentRequest("Test Reply", 1L);
        PostAndCommentUserResponse user = PostAndCommentUserResponse.from(testUser);
        CommentResponse response = CommentResponse.builder()
            .id(2L)
            .content("Test Reply")
            .user(user)
            .parentCommentId(1L)
            .build();
        given(commentService.create(any(), any(), any(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/posts/{postId}/comments", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2L))
            .andExpect(jsonPath("$.content").value("Test Reply"))
            .andExpect(jsonPath("$.parentCommentId").value(1L))
            .andDo(document("comment-reply-create",
                resource(ResourceSnippetParameters.builder()
                    .tag("댓글")
                    .summary("답글 생성 API")
                    .description("기존 댓글에 대한 답글을 생성합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                    .requestFields(
                        fieldWithPath("content").description("답글 내용"),
                        fieldWithPath("parentCommentId").description("부모 댓글 ID")
                    )
                    .responseFields(
                        fieldWithPath("id").description("답글 ID"),
                        fieldWithPath("content").description("답글 내용"),
                        fieldWithPath("user.id").description("작성자 ID"),
                        fieldWithPath("user.username").description("작성자 이름"),
                        fieldWithPath("user.name").description("작성자 표시 이름"),
                        fieldWithPath("parentCommentId").description("부모 댓글 ID"),
                        fieldWithPath("replies").description("답글 목록").optional(),
                        fieldWithPath("createdAt").description("답글 생성 시간"),
                        fieldWithPath("modifiedAt").description("답글 수정 시간")
                    )
                    .requestSchema(schema("CommentRequest"))
                    .responseSchema(schema("CommentResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 댓글_수정_성공() throws Exception {
        // given
        CommentRequest request = new CommentRequest("Updated Comment", null);
        given(commentService.update(any(), any())).willReturn(testCommentResponse);

        // when & then
        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.content").value("Test Comment"))
            .andDo(document("comment-update",
                resource(ResourceSnippetParameters.builder()
                    .tag("댓글")
                    .summary("댓글 수정 API")
                    .description("기존 댓글을 수정합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID"),
                        parameterWithName("commentId").description("댓글 ID")
                    )
                    .requestFields(
                        fieldWithPath("content").description("수정할 댓글 내용"),
                        fieldWithPath("parentCommentId").description("부모 댓글 ID")
                    )
                    .responseFields(
                        fieldWithPath("id").description("댓글 ID"),
                        fieldWithPath("content").description("댓글 내용"),
                        fieldWithPath("user.id").description("작성자 ID"),
                        fieldWithPath("user.username").description("작성자 이름"),
                        fieldWithPath("user.name").description("작성자 표시 이름"),
                        fieldWithPath("parentCommentId").description("부모 댓글 ID"),
                        fieldWithPath("replies").description("답글 목록").optional(),
                        fieldWithPath("createdAt").description("댓글 생성 시간"),
                        fieldWithPath("modifiedAt").description("댓글 수정 시간")
                    )
                    .requestSchema(schema("CommentRequest"))
                    .responseSchema(schema("CommentResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 댓글_수정_실패_권한_없음() throws Exception {
        // given
        CommentRequest request = new CommentRequest("Updated Comment", null);
        given(commentService.update(any(), any())).willThrow(new NotCommentOwnerException());

        // when & then
        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(PostError.COMMENT_NOT_OWNED.getMessage()))
            .andExpect(jsonPath("$.code").value(PostError.COMMENT_NOT_OWNED.name()))
            .andExpect(jsonPath("$.status").value(PostError.COMMENT_NOT_OWNED.getStatus().value()))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.details").isMap())
            .andDo(document("comment-update-failure-not-owner",
                resource(ResourceSnippetParameters.builder()
                    .tag("댓글")
                    .summary("댓글 수정 실패 API")
                    .description("권한이 없는 경우 댓글 수정에 실패합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID"),
                        parameterWithName("commentId").description("댓글 ID")
                    )
                    .requestFields(
                        fieldWithPath("content").description("수정할 댓글 내용"),
                        fieldWithPath("parentCommentId").description("부모 댓글 ID")
                    )
                    .responseFields(
                        fieldWithPath("message").description("에러 메시지"),
                        fieldWithPath("status").description("HTTP 상태 코드"),
                        fieldWithPath("code").description("에러 코드"),
                        fieldWithPath("timestamp").description("에러 발생 시각"),
                        fieldWithPath("details").description("추가 에러 정보"),
                        fieldWithPath("details.message").description("추가 에러 메세지")
                    )
                    .requestSchema(schema("CommentRequest"))
                    .responseSchema(schema("ErrorResponse"))
                    .build()
                )
            ));
    }

    @Test
    void 댓글_삭제_성공() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", 1L, 1L))
            .andExpect(status().isOk())
            .andDo(document("comment-delete",
                resource(ResourceSnippetParameters.builder()
                    .tag("댓글")
                    .summary("댓글 삭제 API")
                    .description("댓글을 삭제합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID"),
                        parameterWithName("commentId").description("댓글 ID")
                    )
                    .responseFields()
                    .build()
                )
            ));
    }

    @Test
    void 댓글_삭제_실패_존재하지_않음() throws Exception {
        // given
        doThrow(new CommentNotFoundException()).when(commentService).delete(any());

        // when & then
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", 1L, 1L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(PostError.COMMENT_NOT_FOUND.getMessage()))
            .andExpect(jsonPath("$.code").value(PostError.COMMENT_NOT_FOUND.name()))
            .andExpect(jsonPath("$.status").value(PostError.COMMENT_NOT_FOUND.getStatus().value()))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.details").isMap())
            .andDo(document("comment-delete-failure-not-found",
                resource(ResourceSnippetParameters.builder()
                    .tag("댓글")
                    .summary("댓글 삭제 실패 API")
                    .description("존재하지 않는 댓글 삭제에 실패합니다")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID"),
                        parameterWithName("commentId").description("댓글 ID")
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
} 