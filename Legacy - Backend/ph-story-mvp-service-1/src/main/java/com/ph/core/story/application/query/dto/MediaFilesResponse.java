package com.ph.core.story.application.query.dto;

import com.ph.core.story.domain.model.MediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class MediaFilesResponse {

    private Long id;

    private Long userId;

    private Long categoryId;

    private Long storyMediaId;

    private MediaType mediaType;

    private String urlPath;

    private String thumbnailUrl;

    private Long fileSize;

    private String title;

    private Instant createdDate;

    private Instant modifiedDate;
}
