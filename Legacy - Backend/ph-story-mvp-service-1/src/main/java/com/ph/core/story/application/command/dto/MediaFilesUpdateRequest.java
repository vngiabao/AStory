package com.ph.core.story.application.command.dto;

import lombok.Data;

@Data
public class MediaFilesUpdateRequest {

    private Long userId;

    private Long categoryId;

    private String mediaType;

    private String urlPath;

    private String thumbnailUrl;

    private Long fileSize;

    private String title;

    private Boolean clearCategory;
}
