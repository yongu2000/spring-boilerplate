package com.boilerplate.boilerplate.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinResponse {

    private Long id;
    private String username;

}
