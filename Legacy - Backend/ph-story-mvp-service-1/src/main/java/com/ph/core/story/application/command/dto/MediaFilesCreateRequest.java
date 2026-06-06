package com.ph.core.story.application.command.dto;

import com.ph.core.story.domain.model.MediaType;
import lombok.Data;

@Data
public class MediaFilesCreateRequest {

    private Long userId;

    private Long categoryId;

    private MediaType mediaType;

    private String urlPath;

    private String thumbnailUrl;

    private Long fileSize;

    private String title;
}
