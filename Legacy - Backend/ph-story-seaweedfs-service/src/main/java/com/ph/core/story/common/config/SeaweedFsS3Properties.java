package com.ph.core.story.common.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "seaweedfs.s3")
public class SeaweedFsS3Properties {

    @NotBlank
    private String endpoint;

    @NotBlank
    private String accessKey;

    @NotBlank
    private String secretKey;

    @NotBlank
    private String region = "us-east-1";

    @NotBlank
    private String bucketName;

    @Min(1)
    @Max(10240)
    private int maxFileSizeMb = 20;

    @NotEmpty
    private List<String> allowedExtensions = new ArrayList<>();

    /**
     * Default presign expiry (seconds).
     */
    @Min(10)
    @Max(86400)
    private long presignDefaultExpirySeconds = 900;

    private Http http = new Http();

    @Data
    public static class Http {
        @Min(100)
        @Max(60000)
        private int connectTimeoutMs = 3000;

        @Min(100)
        @Max(300000)
        private int readTimeoutMs = 30000;

        @Min(100)
        @Max(60000)
        private int connectionAcquireTimeoutMs = 5000;

        @Min(1)
        @Max(500)
        private int maxConnections = 200;
    }
}
