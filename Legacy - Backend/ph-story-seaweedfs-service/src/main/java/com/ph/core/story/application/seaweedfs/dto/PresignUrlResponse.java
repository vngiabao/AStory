package com.ph.core.story.application.seaweedfs.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PresignUrlResponse {
    private final String method;
    private final String url;
    private final long expiresInSeconds;
}

