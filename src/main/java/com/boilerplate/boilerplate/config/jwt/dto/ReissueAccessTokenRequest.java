package com.boilerplate.boilerplate.config.jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReissueAccessTokenRequest {

    private String refreshToken;
}
