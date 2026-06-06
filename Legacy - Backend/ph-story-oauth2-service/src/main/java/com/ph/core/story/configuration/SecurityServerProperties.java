package com.ph.core.story.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties(prefix = "security")
@Data
public class SecurityServerProperties {

    private Jwt jwt;
    private Login login;

    @Data
    public static class Jwt {
        private String audience;
    }

    @Data
    public static class Login {
        private Long maxAttempts;
        private Long lockDurationMinutes;
    }
}
