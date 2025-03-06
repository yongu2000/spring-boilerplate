package com.boilerplate.boilerplate.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequest {

    private String email;
    private String username;
    private String password;
    private String name;
}
