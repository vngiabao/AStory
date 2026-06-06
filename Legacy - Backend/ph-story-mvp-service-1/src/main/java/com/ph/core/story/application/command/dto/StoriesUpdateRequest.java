package com.ph.core.story.application.command.dto;

import lombok.Data;

@Data
public class StoriesUpdateRequest {

    private Long userId;

    // private Long profileId;

    private Long catId;

    private String title;

    private String content;

    private Boolean clearCategory;
}
