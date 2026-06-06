package com.ph.core.story.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http.authorizeExchange(ex -> ex.pathMatchers("/actuator/**").permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(HttpMethod.POST, "/ph-story-users-service/api/v1/users/register")
                .permitAll()
                .pathMatchers(HttpMethod.POST,
                        "/ph-story-users-service/api/v1/users/password-reset/send-otp")
                .permitAll()
                .pathMatchers(HttpMethod.POST,
                        "/ph-story-users-service/api/v1/users/password-reset/verify-otp")
                .permitAll()
                .pathMatchers(HttpMethod.POST,
                        "/ph-story-users-service/api/v1/users/password-reset/confirm")
                .permitAll()
                .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
