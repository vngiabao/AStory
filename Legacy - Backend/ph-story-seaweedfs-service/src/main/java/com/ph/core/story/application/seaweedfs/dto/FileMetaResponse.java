package com.ph.core.story.application.seaweedfs.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class FileMetaResponse {
    private final String key;
    private final String bucket;
    private final String eTag;
    private final Long contentLength;
    private final String contentType;
    private final Instant lastModified;
    private final Map<String, String> metadata;
}

