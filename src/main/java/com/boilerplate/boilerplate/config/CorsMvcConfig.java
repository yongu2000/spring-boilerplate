package com.boilerplate.boilerplate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry
            // CORS를 적용할 URL 패턴
            .addMapping("/**")
            // 응답에 노출되는 헤더
            .exposedHeaders("Authorization", "Set-Cookie", "x-reissue-token")
            // 자원 공유를 허락할 origin (프론트)
            .allowCredentials(true)
            .allowedOrigins("http://localhost:3000");
    }
}
