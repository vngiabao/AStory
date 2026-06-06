package com.ph.core.story.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app.cors") // Chỉ chịu trách nhiệm nhánh app.cors
@Data // Hoặc dùng Getter/Setter thủ công
public class AppCorsProperties {
    private List<String> allowedOrigins;
}
