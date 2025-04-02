package com.boilerplate.boilerplate.domain.user.service;

import static com.boilerplate.boilerplate.utils.TestReflectionUtil.setId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.user.dto.EmailDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.dto.PublicUserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UpdateUserProfileRequest;
import com.boilerplate.boilerplate.domain.user.dto.UserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UsernameDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.exception.InvalidPasswordException;
import com.boilerplate.boilerplate.domain.user.exception.UserDetailNotFoundException;
import com.boilerplate.boilerplate.domain.user.exception.UserNotFoundException;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 서비스 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private CustomUserDetails userDetails;

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

        userDetails = new CustomUserDetails(testUser);
    }

    @Test
    void 사용자_프로필_조회_성공() {
        // given
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(userRepository.findByUsername(any())).willReturn(Optional.of(testUser));

        // when
        UserResponse response = userService.getUserProfile();

        // then
        assertThat(response.getId()).isEqualTo(testUser.getId());
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(response.getName()).isEqualTo(testUser.getName());
        assertThat(response.getBio()).isEqualTo(testUser.getBio());
        assertThat(response.getProfileImageUrl()).isEqualTo(testUser.getProfileImage().getUrl());
        assertThat(response.getCreatedAt()).isEqualTo(testUser.getCreatedAt());
    }

    @Test
    void 사용자_프로필_조회_실패_인증_정보_없음() {
        // given
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("anonymous");

        // when & then
        assertThatThrownBy(() -> userService.getUserProfile())
            .isInstanceOf(UserDetailNotFoundException.class);
    }

    @Test
    void 공개_사용자_정보_조회_성공() {
        // given
        given(userRepository.findByUsername(any())).willReturn(Optional.of(testUser));

        // when
        PublicUserResponse response = userService.getPublicUserByUsername("test");

        // then
        assertThat(response.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(response.getName()).isEqualTo(testUser.getName());
        assertThat(response.getBio()).isEqualTo(testUser.getBio());
        assertThat(response.getProfileImageUrl()).isEqualTo(testUser.getProfileImage().getUrl());
        assertThat(response.getCreatedAt()).isEqualTo(testUser.getCreatedAt());
    }

    @Test
    void 공개_사용자_정보_조회_실패_사용자_없음() {
        // given
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getPublicUserByUsername("test"))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 이메일_중복_체크_성공_중복_없음() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when
        EmailDuplicateCheckResponse response = userService.checkEmailDuplicate("test@example.com");

        // then
        assertThat(response.isDuplicate()).isFalse();
    }

    @Test
    void 이메일_중복_체크_성공_중복_있음() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.of(testUser));

        // when
        EmailDuplicateCheckResponse response = userService.checkEmailDuplicate("test@example.com");

        // then
        assertThat(response.isDuplicate()).isTrue();
    }

    @Test
    void 사용자명_중복_체크_성공_중복_없음() {
        // given
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());

        // when
        UsernameDuplicateCheckResponse response = userService.checkUsernameDuplicate("test");

        // then
        assertThat(response.isDuplicate()).isFalse();
    }

    @Test
    void 사용자명_중복_체크_성공_중복_있음() {
        // given
        given(userRepository.findByUsername(any())).willReturn(Optional.of(testUser));

        // when
        UsernameDuplicateCheckResponse response = userService.checkUsernameDuplicate("test");

        // then
        assertThat(response.isDuplicate()).isTrue();
    }

    @Test
    void ID로_사용자_조회_성공() {
        // given
        given(userRepository.findById(any())).willReturn(Optional.of(testUser));

        // when
        User user = userService.findById(1L);

        // then
        assertThat(user.getId()).isEqualTo(testUser.getId());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void ID로_사용자_조회_실패_사용자_없음() {
        // given
        given(userRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findById(1L))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 사용자명으로_사용자_조회_성공() {
        // given
        given(userRepository.findByUsername(any())).willReturn(Optional.of(testUser));

        // when
        User user = userService.findByUsername("test");

        // then
        assertThat(user.getId()).isEqualTo(testUser.getId());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void 사용자명으로_사용자_조회_실패_사용자_없음() {
        // given
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findByUsername("test"))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 이메일로_사용자_조회_성공() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.of(testUser));

        // when
        User user = userService.findByEmail("test@example.com");

        // then
        assertThat(user.getId()).isEqualTo(testUser.getId());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void 이메일로_사용자_조회_실패_사용자_없음() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findByEmail("test@example.com"))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 사용자_프로필_수정_성공() {
        // given
        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
            "New Name", "New Bio", "new@example.com", "newusername", "currentPassword", "newPassword");
        given(userRepository.findByUsername(any())).willReturn(Optional.of(testUser));
        given(bCryptPasswordEncoder.matches(any(), any())).willReturn(true);
        given(bCryptPasswordEncoder.encode(any())).willReturn("newEncodedPassword");
        given(userRepository.save(any())).willReturn(testUser);

        // when
        UserResponse response = userService.updateUserProfile("test", request);

        // then
        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getBio()).isEqualTo("New Bio");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getUsername()).isEqualTo("newusername");
        verify(userRepository).save(any());
    }

    @Test
    void 사용자_프로필_수정_실패_비밀번호_불일치() {
        // given
        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
            "New Name", "New Bio", "new@example.com", "newusername", "wrongPassword", "newPassword");
        given(userRepository.findByUsername(any())).willReturn(Optional.of(testUser));
        given(bCryptPasswordEncoder.matches(any(), any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.updateUserProfile("test", request))
            .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void 사용자_프로필_수정_실패_사용자_없음() {
        // given
        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
            "New Name", "New Bio", "new@example.com", "newusername", "currentPassword", "newPassword");
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUserProfile("test", request))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 사용자_프로필_수정_성공_비밀번호_변경_현재_비밀번호_일치() {
        // given
        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
            "New Name", "New Bio", "new@example.com", "newusername", "currentPassword", "newPassword");
        given(userRepository.findByUsername(any())).willReturn(Optional.of(testUser));
        given(bCryptPasswordEncoder.matches(any(), any())).willReturn(true);
        given(bCryptPasswordEncoder.encode(any())).willReturn("newEncodedPassword");
        given(userRepository.save(any())).willReturn(testUser);

        // when
        UserResponse response = userService.updateUserProfile("test", request);

        // then
        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getBio()).isEqualTo("New Bio");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getUsername()).isEqualTo("newusername");
        verify(userRepository).save(any());
        verify(bCryptPasswordEncoder).matches("currentPassword", "encodedPassword");
        verify(bCryptPasswordEncoder).encode("newPassword");
    }

    @Test
    void 사용자_프로필_수정_성공_비밀번호_변경_없음() {
        // given
        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
            "New Name", "New Bio", "new@example.com", "newusername", null, null);
        given(userRepository.findByUsername(any())).willReturn(Optional.of(testUser));
        given(userRepository.save(any())).willReturn(testUser);

        // when
        UserResponse response = userService.updateUserProfile("test", request);

        // then
        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getBio()).isEqualTo("New Bio");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getUsername()).isEqualTo("newusername");
        verify(userRepository).save(any());
        verify(bCryptPasswordEncoder, never()).matches(any(), any());
        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void 사용자_프로필_수정_성공_비밀번호_변경_현재_비밀번호_없음() {
        // given
        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
            "New Name", "New Bio", "new@example.com", "newusername", "", "newPassword");
        given(userRepository.findByUsername(any())).willReturn(Optional.of(testUser));
        given(bCryptPasswordEncoder.encode(any())).willReturn("newEncodedPassword");
        given(userRepository.save(any())).willReturn(testUser);

        // when
        UserResponse response = userService.updateUserProfile("test", request);

        // then
        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getBio()).isEqualTo("New Bio");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getUsername()).isEqualTo("newusername");
        verify(userRepository).save(any());
        verify(bCryptPasswordEncoder, never()).matches(any(), any());
        verify(bCryptPasswordEncoder).encode("newPassword");
    }
} 