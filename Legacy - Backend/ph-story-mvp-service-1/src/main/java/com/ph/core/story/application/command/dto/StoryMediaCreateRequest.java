package com.ph.core.story.application.command.dto;

import lombok.Data;

@Data
public class StoryMediaCreateRequest {

    private Long storyId;

    private Long mediaId;

    private String caption;
}
