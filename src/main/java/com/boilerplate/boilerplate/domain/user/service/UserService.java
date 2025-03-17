package com.boilerplate.boilerplate.domain.user.service;

import com.boilerplate.boilerplate.domain.auth.CustomUserDetails;
import com.boilerplate.boilerplate.domain.user.dto.UserResponse;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.exception.UserDetailNotFoundException;
import com.boilerplate.boilerplate.domain.user.exception.UserNotFoundException;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            User user = findByUsername(userDetails.getUsername()); // DB 조회
            return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .build();
        } else {
            throw new UserDetailNotFoundException();
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(UserNotFoundException::new);
    }
}
