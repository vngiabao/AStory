package com.ph.core.story.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    /**
     * Cấu hình AuditorAware (bắt buộc nếu dùng CreatedBy)
     * @return
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            if (auth == null || !auth.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }

            return Optional.ofNullable(auth.getName());
        };
    }
}


