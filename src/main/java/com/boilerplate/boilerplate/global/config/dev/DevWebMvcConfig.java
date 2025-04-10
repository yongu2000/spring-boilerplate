package com.boilerplate.boilerplate.global.config.dev;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("dev")
public class DevWebMvcConfig implements WebMvcConfigurer {

    // 정적 리소스 등록
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // 클라이언트가 접근할 경로
            .addResourceLocations("file:uploads/"); // 실제 로컬 디렉토리
    }
}
