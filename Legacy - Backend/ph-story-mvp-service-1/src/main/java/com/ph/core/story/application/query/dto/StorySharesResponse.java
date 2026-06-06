package com.ph.core.story.application.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class StorySharesResponse {

    private Long id;

    private Long storyId;

    private Long sharedUserId;

    private Boolean deleted;

    private Instant createdDate;

    private Instant modifiedDate;
}
