package com.boilerplate.boilerplate.domain.auth.jwt.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Claim {
    TOKEN_TYPE("token_type"),
    ID("id"),
    EMAIL("email"),
    USERNAME("username"),
    NAME("name"),
    ROLE("role");

    private final String value;
}
