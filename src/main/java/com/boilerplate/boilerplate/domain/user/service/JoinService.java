package com.boilerplate.boilerplate.domain.user.service;

import com.boilerplate.boilerplate.domain.user.dto.JoinRequest;
import com.boilerplate.boilerplate.domain.user.dto.JoinResponse;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.exception.DuplicateUserException;
import com.boilerplate.boilerplate.domain.user.exception.EmailNotVerifiedException;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinResponse join(JoinRequest request) {
        validateEmailExistence(request.getEmail());
        validateEmailVerification(request.getEmail());

        String username = generateUniqueUsername(request.getEmail());
        String name = parseEmailToUsername(request.getEmail());
        User newUser = User.builder()
            .email(request.getEmail())
            .username(username)
            .password(bCryptPasswordEncoder.encode(request.getPassword()))
            .name(name)
            .role(Role.USER)
            .build();
        userRepository.save(newUser);
        return JoinResponse.builder()
            .id(newUser.getId())
            .username(newUser.getUsername())
            .build();
    }

    private void validateEmailVerification(String email) {
        String value = redisTemplate.opsForValue().get("EMAIL:VERIFIED:" + email);
        if (!Boolean.parseBoolean(value)) {
            throw new EmailNotVerifiedException();
        }
    }

    private void validateEmailExistence(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new DuplicateUserException();
        }
    }

    private String generateUniqueUsername(String email) {
        String baseUsername = parseEmailToUsername(email);
        String username = baseUsername;
        int randomNumber;

        while (userRepository.findByUsername(username).isPresent()) {
            randomNumber = (int) (Math.random() * 10000);
            username = baseUsername + randomNumber;
        }

        return username;
    }

    private void validateUsernameExistence(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new DuplicateUserException();
        }
    }

    private String parseEmailToUsername(String email) {
        return email.split("@")[0];
    }
}
