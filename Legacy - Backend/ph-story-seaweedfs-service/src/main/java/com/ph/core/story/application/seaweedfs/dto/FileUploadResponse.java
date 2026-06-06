package com.ph.core.story.application.seaweedfs.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadResponse {
    private final String bucket;
    private final String key;
    private final String eTag;
    private final long size;
    private final String contentType;
}

