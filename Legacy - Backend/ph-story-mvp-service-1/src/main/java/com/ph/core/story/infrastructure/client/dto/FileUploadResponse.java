package com.ph.core.story.infrastructure.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadResponse {

    private String bucket;

    private String key;

    private String eTag;

    private long size;

    private String contentType;
}
