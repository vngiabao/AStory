package com.ph.core.story.configuration;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "app.security")
@Data
public class AuthServerProperties {

    private String issuer;
    private Clients clients;

    @Data
    public static class Clients {
        private M2m m2m;
        private Spa spa;
        private Mobile mobile;
    }

    @Data
    public static class M2m {
        private long accessTokenMinutes;
        private String clientId;
        private String clientSecret;
        private List<String> scopes;
    }

    @Data
    public static class Spa {
        private long accessTokenMinutes;
        private String clientId;
        private String redirectUri;
        private String logoutRedirectUri;
    }

    @Data
    public static class Mobile {
        private long accessTokenMinutes;
        private long refreshTokenDays;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private List<String> redirectUris;

        public List<String> getEffectiveRedirectUris() {
            if (redirectUris != null && !redirectUris.isEmpty()) {
                return redirectUris;
            }
            if (redirectUri != null && !redirectUri.isBlank()) {
                return List.of(redirectUri);
            }
            return Collections.emptyList();
        }
    }
}
