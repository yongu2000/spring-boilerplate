package com.boilerplate.boilerplate.domain.user.dto;

import com.boilerplate.boilerplate.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PublicUserResponse {

    private String username;
    private String name;
    private String bio;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    public static PublicUserResponse of(User user) {
        return PublicUserResponse.builder()
            .username(user.getUsername())
            .name(user.getName())
            .bio(user.getBio())
            .profileImageUrl(user.getProfileImageUrl())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
