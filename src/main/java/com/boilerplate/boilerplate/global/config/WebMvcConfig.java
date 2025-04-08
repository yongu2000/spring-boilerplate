package com.boilerplate.boilerplate.global.config;

import com.boilerplate.boilerplate.global.converter.StringToEnumConverter;
import com.boilerplate.boilerplate.global.interceptor.LoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverter());
    }

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor())
            .addPathPatterns("/api/**"); // API 경로에만 적용
    }

    // 정적 리소스 등록
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // 클라이언트가 접근할 경로
            .addResourceLocations("file:uploads/"); // 실제 로컬 디렉토리
    }
}
