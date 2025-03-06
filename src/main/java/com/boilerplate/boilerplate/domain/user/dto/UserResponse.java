package com.boilerplate.boilerplate.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String username;
    private String name;
}
