package com.ph.core.story.application.seaweedfs.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ListObjectsResponse {
    private final String bucket;
    private final String prefix;
    private final boolean truncated;
    private final String nextContinuationToken;
    private final List<ObjectItem> items;

    @Getter
    @Builder
    public static class ObjectItem {
        private final String key;
        private final String fileName;
        private final String extension;
        private final Long size;
        private final String eTag;
        private final String lastModified;
    }
}

