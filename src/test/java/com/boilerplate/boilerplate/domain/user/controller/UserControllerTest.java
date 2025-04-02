package com.boilerplate.boilerplate.domain.user.controller;

import static com.boilerplate.boilerplate.utils.TestReflectionUtil.setCreatedAt;
import static com.boilerplate.boilerplate.utils.TestReflectionUtil.setId;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.boilerplate.domain.auth.jwt.service.AccessTokenService;
import com.boilerplate.boilerplate.domain.auth.jwt.service.RefreshTokenService;
import com.boilerplate.boilerplate.domain.user.dto.EmailDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.dto.PublicUserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UpdateUserProfileRequest;
import com.boilerplate.boilerplate.domain.user.dto.UserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UsernameDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import com.boilerplate.boilerplate.global.config.JwtConfig;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@DisplayName("사용자 컨트롤러 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private JwtConfig jwtConfig;

    private User testUser;
    private UserResponse testUserResponse;
    private PublicUserResponse testPublicUserResponse;
    private UpdateUserProfileRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .email("test@example.com")
            .username("test")
            .name("Test User")
            .password("encodedPassword")
            .role(Role.USER)
            .build();

        testUser.updateProfile(null, "Test Bio", null, null);
        setId(testUser, 1L);
        setCreatedAt(testUser, LocalDateTime.now());

        testUserResponse = UserResponse.builder()
            .id(testUser.getId())
            .email(testUser.getEmail())
            .username(testUser.getUsername())
            .name(testUser.getName())
            .bio(testUser.getBio())
            .profileImageUrl(testUser.getProfileImage().getUrl())
            .createdAt(testUser.getCreatedAt())
            .build();

        testPublicUserResponse = PublicUserResponse.builder()
            .username(testUser.getUsername())
            .name(testUser.getName())
            .bio(testUser.getBio())
            .profileImageUrl(testUser.getProfileImage().getUrl())
            .createdAt(testUser.getCreatedAt())
            .build();

        testUpdateRequest = new UpdateUserProfileRequest(
            "New Name", "New Bio", "new@example.com", "newusername", "currentPassword", "newPassword");
    }

    @Test
    void 사용자_프로필_조회_성공() throws Exception {
        // given
        given(userService.getUserProfile()).willReturn(testUserResponse);

        // when & then
        mockMvc.perform(get("/api/user/my"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testUser.getId()))
            .andExpect(jsonPath("$.email").value(testUser.getEmail()))
            .andExpect(jsonPath("$.username").value(testUser.getUsername()))
            .andExpect(jsonPath("$.name").value(testUser.getName()))
            .andExpect(jsonPath("$.bio").value(testUser.getBio()))
            .andExpect(jsonPath("$.profileImageUrl").value(testUser.getProfileImage().getUrl()))
            .andDo(document("get-user-private-profile",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("사용자")
                        .summary("내 프로필 조회 API")
                        .description("자신의 프로필 정보를 조회합니다")
                        .responseFields(
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                            fieldWithPath("username").type(JsonFieldType.STRING).description("사용자명"),
                            fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                            fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개"),
                            fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간")
                        )
                        .responseSchema(schema("UserResponse"))
                        .build()
                )
            ));
    }

    @Test
    void 사용자_프로필_수정_성공() throws Exception {
        // given
        given(userService.updateUserProfile(any(), any())).willReturn(testUserResponse);
        given(userService.findByUsername(any())).willReturn(testUser);
        given(accessTokenService.createAccessToken(any())).willReturn("newAccessToken");
        given(refreshTokenService.createRefreshToken(any(), any())).willReturn("newRefreshToken");
        given(jwtConfig.getRememberMeCookieName()).willReturn("rememberMe");
        given(jwtConfig.getRefreshTokenCookieName()).willReturn("refreshToken");
        given(jwtConfig.getHeaderAuthorization()).willReturn("Authorization");
        given(jwtConfig.getAccessTokenPrefix()).willReturn("Bearer ");

        // when & then
        mockMvc.perform(put("/api/user/{username}", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUpdateRequest))
                .cookie(new Cookie("rememberMe", "true")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testUser.getId()))
            .andExpect(jsonPath("$.email").value(testUser.getEmail()))
            .andExpect(jsonPath("$.username").value(testUser.getUsername()))
            .andExpect(jsonPath("$.name").value(testUser.getName()))
            .andExpect(jsonPath("$.bio").value(testUser.getBio()))
            .andExpect(jsonPath("$.profileImageUrl").value(testUser.getProfileImage().getUrl()))
            .andExpect(jsonPath("$.createdAt").exists())
            .andDo(document("update-user-profile",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("사용자")
                        .summary("프로필 수정 API")
                        .description("사용자의 프로필 정보를 수정합니다")
                        .pathParameters(
                            parameterWithName("username").description("사용자명")
                        )
                        .requestFields(
                            fieldWithPath("name").type(JsonFieldType.STRING).description("새로운 이름").optional(),
                            fieldWithPath("bio").type(JsonFieldType.STRING).description("새로운 자기소개").optional(),
                            fieldWithPath("email").type(JsonFieldType.STRING).description("새로운 이메일").optional(),
                            fieldWithPath("username").type(JsonFieldType.STRING).description("새로운 사용자명").optional(),
                            fieldWithPath("currentPassword").type(JsonFieldType.STRING).description("현재 비밀번호")
                                .optional(),
                            fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새로운 비밀번호").optional()
                        )
                        .responseFields(
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                            fieldWithPath("username").type(JsonFieldType.STRING).description("사용자명"),
                            fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                            fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개"),
                            fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간")
                        )
                        .requestSchema(schema("UpdateUserProfileRequest"))
                        .responseSchema(schema("UserResponse"))
                        .build()
                )
            ));
    }

    @Test
    void 이메일_중복_체크_성공() throws Exception {
        // given
        given(userService.checkEmailDuplicate(any())).willReturn(new EmailDuplicateCheckResponse(false));

        // when & then
        mockMvc.perform(get("/api/user/check/email/{email}", "test@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isDuplicate").value(false))
            .andDo(document("check-email-duplicate",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("사용자")
                        .summary("이메일 중복 체크 API")
                        .description("이메일의 중복 여부를 확인합니다")
                        .pathParameters(
                            parameterWithName("email").description("중복 체크할 이메일")
                        )
                        .responseFields(
                            fieldWithPath("isDuplicate").type(JsonFieldType.BOOLEAN).description("중복 여부")
                        )
                        .responseSchema(schema("EmailDuplicateCheckResponse"))
                        .build()
                )
            ));
    }

    @Test
    void 사용자명_중복_체크_성공() throws Exception {
        // given
        given(userService.checkUsernameDuplicate(any())).willReturn(new UsernameDuplicateCheckResponse(false));

        // when & then
        mockMvc.perform(get("/api/user/check/username/{username}", "test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isDuplicate").value(false))
            .andDo(document("check-username-duplicate",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("사용자")
                        .summary("사용자명 중복 체크 API")
                        .description("사용자명의 중복 여부를 확인합니다")
                        .pathParameters(
                            parameterWithName("username").description("중복 체크할 사용자명")
                        )
                        .responseFields(
                            fieldWithPath("isDuplicate").type(JsonFieldType.BOOLEAN).description("중복 여부")
                        )
                        .responseSchema(schema("UsernameDuplicateCheckResponse"))
                        .build()
                )
            ));
    }

    @Test
    void 공개_사용자_정보_조회_성공() throws Exception {
        // given
        given(userService.getPublicUserByUsername(any())).willReturn(testPublicUserResponse);

        // when & then
        mockMvc.perform(get("/api/user/{username}", "test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(testUser.getUsername()))
            .andExpect(jsonPath("$.name").value(testUser.getName()))
            .andExpect(jsonPath("$.bio").value(testUser.getBio()))
            .andExpect(jsonPath("$.profileImageUrl").value(testUser.getProfileImage().getUrl()))
            .andExpect(jsonPath("$.createdAt").exists())
            .andDo(document("get-public-user",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("사용자")
                        .summary("공개 프로필 조회 API")
                        .description("다른 사용자의 공개 프로필 정보를 조회합니다")
                        .pathParameters(
                            parameterWithName("username").description("사용자명")
                        )
                        .responseFields(
                            fieldWithPath("username").type(JsonFieldType.STRING).description("사용자명"),
                            fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                            fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개"),
                            fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간")
                        )
                        .responseSchema(schema("PublicUserResponse"))
                        .build()
                )
            ));
    }
} 