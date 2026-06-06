package com.ph.core.story.application.command.dto;

import lombok.Data;

@Data
public class StorySharesCreateRequest {

    private Long storyId;

    private Long sharedUserId;
}
