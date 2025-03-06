package com.boilerplate.boilerplate.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class JoinResponse {

    private Long id;
    private String username;

}
