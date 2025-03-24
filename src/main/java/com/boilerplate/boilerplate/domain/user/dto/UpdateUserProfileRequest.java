package com.boilerplate.boilerplate.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserProfileRequest {

    private String name;
    private String bio;
    private String email;
    private String username;
    private String currentPassword;
    private String newPassword;
}
