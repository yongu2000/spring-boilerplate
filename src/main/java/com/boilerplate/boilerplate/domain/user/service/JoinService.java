package com.boilerplate.boilerplate.domain.user.service;

import com.boilerplate.boilerplate.domain.user.dto.JoinRequest;
import com.boilerplate.boilerplate.domain.user.dto.JoinResponse;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.exception.UserError;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinResponse join(JoinRequest request) {
        validateUsernameExistence(request.getUsername());
        User newUser = User.builder()
            .email(request.getEmail())
            .username(request.getUsername())
            .password(bCryptPasswordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .role(Role.USER)
            .build();
        userRepository.save(newUser);
        return JoinResponse.builder()
            .id(newUser.getId())
            .username(newUser.getUsername())
            .build();
    }

    private void validateUsernameExistence(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new IllegalArgumentException(UserError.ALREADY_EXIST.getMessage());
        }
    }
}
