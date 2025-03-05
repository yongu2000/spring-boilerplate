package com.boilerplate.boilerplate.domain.user.service;

import com.boilerplate.boilerplate.config.jwt.JwtUserDetails;
import com.boilerplate.boilerplate.domain.user.dto.UserResponse;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.exception.UserError;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
            JwtUserDetails userDetails = (JwtUserDetails) principal;
            User user = findByUsername(userDetails.getUsername()); // DB 조회
            return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getName());
        } else {
            throw new RuntimeException("유효한 인증 정보가 없습니다.");
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
            UserError.NO_SUCH_USER.getMessage()));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException(UserError.NO_SUCH_USER.getMessage()));
    }
}
