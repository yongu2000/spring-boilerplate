package com.boilerplate.boilerplate.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String role;

    public static Role of(String string) {
        for (Role value : Role.values()) {
            if (value.getRole().equals(string)) {
                return value;
            }
        }
        return null;
    }
}
