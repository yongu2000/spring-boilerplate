package com.boilerplate.boilerplate.domain.user.service;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.user.dto.EmailDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.dto.PublicUserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UpdateUserProfileRequest;
import com.boilerplate.boilerplate.domain.user.dto.UserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UsernameDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.exception.InvalidPasswordException;
import com.boilerplate.boilerplate.domain.user.exception.UserDetailNotFoundException;
import com.boilerplate.boilerplate.domain.user.exception.UserNotFoundException;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserCascadeDeleteService userCascadeDeleteService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserResponse getUserProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            User user = findByUsername(userDetails.getUsername()); // DB 조회
            return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .build();
        } else {
            throw new UserDetailNotFoundException();
        }
    }

    public PublicUserResponse getPublicUserByUsername(String username) {
        return PublicUserResponse.of(findByUsername(username));
    }

    public EmailDuplicateCheckResponse checkEmailDuplicate(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return new EmailDuplicateCheckResponse(true);
        }
        return new EmailDuplicateCheckResponse(false);
    }

    public UsernameDuplicateCheckResponse checkUsernameDuplicate(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            return new UsernameDuplicateCheckResponse(true);
        }
        return new UsernameDuplicateCheckResponse(false);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(UserNotFoundException::new);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);
    }

    // 권한 확인 로직 필요 (admin, 본인)
    public UserResponse updateUserProfile(String targetUsername, UpdateUserProfileRequest request) {
        User user = findByUsername(targetUsername);

        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (!request.getCurrentPassword().isEmpty() && !bCryptPasswordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
                throw new InvalidPasswordException();
            }
            user.updatePassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        }

        user.updateProfile(
            request.getName(),
            request.getBio(),
            request.getEmail(),
            request.getUsername(),
            request.getProfileImageUrl()
        );

        return UserResponse.of(userRepository.save(user));
    }

    // 권한 확인 로직 필요 (admin, 본인)
    public void deleteByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        userCascadeDeleteService.deleteByUser(user);
        userRepository.delete(user);
    }

//    public void uploadProfileImage(String username, MultipartFile file) {
//        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
//        Image image = imageService.uploadImage(file);
//        user.changeProfileImage(image);
//    }
}
