package com.boilerplate.boilerplate.domain.jwt;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {

    private String issuer;
    private String secretKey;

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String ACCESS_TOKEN_PREFIX = "Bearer ";

    public static final Duration ACCESS_TOKEN_EXPIRATION_DURATION = Duration.ofMinutes(10);
    public static final Duration REFRESH_TOKEN_EXPIRATION_DURATION = Duration.ofDays(14);

    public static final String REFRESH_TOKEN_NAME = "REFRESH_TOKEN";


}
