package com.boilerplate.boilerplate.domain.auth.jwt.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Claim {
    ID("id"),
    EMAIL("email"),
    USERNAME("username"),
    NAME("name"),
    ROLE("role");

    private final String value;
}
