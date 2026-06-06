package com.ph.core.story.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration.seaweedfs")
public class SeaweedFsClientProperties {

    private String baseUrl;

    private String avatarPrefix = "avatars";
}
