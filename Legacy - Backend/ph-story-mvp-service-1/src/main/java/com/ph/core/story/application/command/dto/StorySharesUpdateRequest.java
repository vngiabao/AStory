package com.ph.core.story.application.command.dto;

import lombok.Data;

@Data
public class StorySharesUpdateRequest {

    private Long storyId;

    private Long sharedUserId;
}
