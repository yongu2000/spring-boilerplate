package com.boilerplate.boilerplate.global.config;

import com.boilerplate.boilerplate.domain.auth.jwt.exception.CustomAccessDeniedHandler;
import com.boilerplate.boilerplate.domain.auth.jwt.exception.CustomAuthenticationEntryPoint;
import com.boilerplate.boilerplate.domain.auth.jwt.filters.JwtAuthenticationFilter;
import com.boilerplate.boilerplate.domain.auth.jwt.filters.JwtLoginFilter;
import com.boilerplate.boilerplate.domain.auth.jwt.service.JwtTokenService;
import com.boilerplate.boilerplate.domain.auth.oauth2.CustomOAuth2UserService;
import com.boilerplate.boilerplate.domain.auth.oauth2.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtTokenService jwtTokenService;
    private final JwtConfig jwtConfig;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(
                corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        // 프론트 서버 주소
                        configuration.setAllowedOrigins(
                            Collections.singletonList("http://localhost:3000"));
                        // GET, POST, 등 요청
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        // 쿠키, Authorization 인증 헤더, TLS client certificates(증명서)를 내포하는 자격 인증 정보
                        configuration.setAllowCredentials(true);
                        // 받을 수 있는 헤더 값
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        // 백엔드에서 프론트로 보낼 데이터들
                        configuration.setExposedHeaders(
                            Arrays.asList("Authorization", "Set-Cookie", "x-reissue-token"));

                        return configuration;
                    }
                }));

        http
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(authenticationEntryPoint)  // 인증 실패
                .accessDeniedHandler(accessDeniedHandler)           // 인가 실패
            );

        //csrf disable
        http.csrf(AbstractHttpConfigurer::disable);

        //From 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        //http basic 인증 방식 disable
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.logout(AbstractHttpConfigurer::disable);

        //경로별 인가 작업
        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/api/login", "/api/join", "/api/token/**", "/uploads/**")
                .permitAll() // 기본 공개 API
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        //oauth2
        http
            .oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                    .userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                })
            );

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenService, jwtConfig),
            JwtLoginFilter.class);

        http
            .addFilterAt(
                new JwtLoginFilter(authenticationManager(authenticationConfiguration),
                    jwtTokenService),
                UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


}