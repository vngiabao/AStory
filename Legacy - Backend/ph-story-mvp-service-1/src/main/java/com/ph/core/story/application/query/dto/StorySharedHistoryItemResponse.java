package com.ph.core.story.application.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class StorySharedHistoryItemResponse {

    public StorySharedHistoryItemResponse(
            Long id,
            Long storyId,
            String title,
            String content,
            Instant storyCreatedDate,
            Instant storyModifiedDate,
            Long recipientId,
            String recipientUsername,
            String recipientFullname,
            String recipientEmail,
            String recipientName,
            String recipientSubtitle,
            Instant sharedDate) {
        this.id = id;
        this.storyId = storyId;
        this.title = title;
        this.content = content;
        this.storyCreatedDate = storyCreatedDate;
        this.storyModifiedDate = storyModifiedDate;
        this.recipientId = recipientId;
        this.recipientUsername = recipientUsername;
        this.recipientFullname = recipientFullname;
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.recipientSubtitle = recipientSubtitle;
        this.sharedDate = sharedDate;
    }

    private Long id;

    private Long storyId;
    private String title;
    private String content;
    private Instant storyCreatedDate;
    private Instant storyModifiedDate;

    private Long recipientId;
    private String recipientUsername;
    private String recipientFullname;
    private String recipientEmail;
    private String recipientName;
    private String recipientSubtitle;

    private Instant sharedDate;
}
