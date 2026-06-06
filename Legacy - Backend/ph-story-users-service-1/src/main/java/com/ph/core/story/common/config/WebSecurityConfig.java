package com.ph.core.story.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                // ===============================
                                // Stateless - JWT
                                // ===============================
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(
                                                session -> session
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // ===============================
                                // Authorization
                                // ===============================
                                .authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/health").permitAll()
                                                .requestMatchers("/actuator/info").permitAll()
                                                // MỞ REGISTER (KHÔNG có prefix)
                                                .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/v1/users/password-reset/send-otp")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/v1/users/password-reset/verify-otp")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/v1/users/password-reset/confirm")
                                                .permitAll()
                                                .anyRequest().authenticated())

                                // ===============================
                                // OAuth2 Resource Server (JWT)
                                // ===============================
                                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))

                                // ===============================
                                // Security Headers
                                // ===============================
                                .headers(headers -> headers

                                                // Content Security Policy (API-safe minimal)
                                                .contentSecurityPolicy(
                                                                csp -> csp.policyDirectives("default-src 'none'; "
                                                                                + "frame-ancestors 'none'; "
                                                                                + "base-uri 'none';"))

                                                // Prevent Clickjacking
                                                .frameOptions(frame -> frame.deny())

                                                // Prevent MIME sniffing
                                                .contentTypeOptions(Customizer.withDefaults())

                                                // Referrer Policy
                                                .referrerPolicy(referrer -> referrer
                                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)));

                return http.build();
        }
}

// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity
// public class WebSecurityConfig {

// @Bean
// public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
// Exception {

// http
// .headers(headers -> headers
// // CSP chỉ có ý nghĩa khi service render HTML.
// .contentSecurityPolicy(csp -> csp
// .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self';
// img-src 'self';")
// )
// )
// .csrf(csrf -> csrf.disable())
// // cấu hình stateless
// .sessionManagement(session -> session
// .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
// )
// //.addFilterBefore(new XSSFilter(), CsrfFilter.class)
// .authorizeHttpRequests(auth -> auth
// .requestMatchers("/actuator/**").permitAll()
// .anyRequest().authenticated()
// )
// // THÊM ĐOẠN NÀY
// .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

// return http.build();
// }
// }
