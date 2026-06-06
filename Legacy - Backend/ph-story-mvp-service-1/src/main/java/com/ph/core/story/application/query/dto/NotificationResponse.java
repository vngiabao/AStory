package com.ph.core.story.application.query.dto;

import com.ph.core.story.domain.model.NotificationType;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {

    private Long id;

    private NotificationType type;

    private String title;

    private String body;

    private Long storyId;

    private Long storyShareId;

    private Long actorUserId;

    private boolean read;

    private Instant readAt;

    private Instant openedAt;

    private Instant createdDate;

    private Instant modifiedDate;
}
