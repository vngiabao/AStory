package com.ph.core.story.application.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class StoryMediaResponse {

    private Long id;

    private Long storyId;

    private Long mediaId;

    private String caption;

    private Boolean deleted;

    private Instant createdDate;

    private Instant modifiedDate;
}
