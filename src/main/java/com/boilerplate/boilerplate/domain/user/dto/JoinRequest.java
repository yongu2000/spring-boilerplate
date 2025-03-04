package com.boilerplate.boilerplate.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequest {

    private String email;
    private String username;
    private String password;
    private String name;
}
