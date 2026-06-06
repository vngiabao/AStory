package com.ph.core.story.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.config.Customizer;
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
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ===============================
                // Authorization
                // ===============================
                .authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/info").permitAll().anyRequest().authenticated())

                // ===============================
                // OAuth2 Resource Server (JWT)
                // ===============================
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))

                // ===============================
                // Security Headers
                // ===============================
                .headers(headers -> headers

                        // Content Security Policy (API-safe minimal)
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'; "
                                + "frame-ancestors 'none'; " + "base-uri 'none';"))

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
