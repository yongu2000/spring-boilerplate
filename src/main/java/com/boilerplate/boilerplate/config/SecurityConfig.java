package com.boilerplate.boilerplate.config;

import com.boilerplate.boilerplate.config.jwt.filters.JwtAuthenticationFilter;
import com.boilerplate.boilerplate.config.jwt.filters.JwtLoginFilter;
import com.boilerplate.boilerplate.config.jwt.filters.JwtLogoutFilter;
import com.boilerplate.boilerplate.config.jwt.service.JwtTokenService;
import com.boilerplate.boilerplate.config.jwt.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

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
                .requestMatchers("/login", "/join", "/", "/api/login", "/api/join", "/api/token/**")
                .permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        http
            .addFilterBefore(new JwtLogoutFilter(jwtTokenService, refreshTokenService),
                LogoutFilter.class);

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenService), JwtLoginFilter.class);

        http
            .addFilterAt(
                new JwtLoginFilter(authenticationManager(authenticationConfiguration),
                    jwtTokenService,
                    refreshTokenService),
                UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

}