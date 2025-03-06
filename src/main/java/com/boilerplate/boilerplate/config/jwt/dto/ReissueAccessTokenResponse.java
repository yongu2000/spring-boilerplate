package com.boilerplate.boilerplate.config.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReissueAccessTokenResponse {

    private String accessToken;
    private String refreshToken;
}
