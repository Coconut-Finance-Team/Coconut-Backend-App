package com.coconut.stock_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/**").permitAll()
                                .requestMatchers("/api/v1/stock/**").permitAll() // /api/v1/stock 하위 모든 경로 허용
                                .anyRequest().authenticated()
                );
                //.csrf().disable(); // CSRF 비활성화 (필요한 경우)

        return http.build();
    }
}