package com.boilerplate.boilerplate.domain.auth.jwt.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Claim {
    TOKEN_TYPE("token_type"),
    ID("id");

    private final String value;
}
