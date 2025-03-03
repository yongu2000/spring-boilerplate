package com.boilerplate.boilerplate.config.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueAccessTokenResponse {

    private String accessToken;
}
