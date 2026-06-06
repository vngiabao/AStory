package com.ph.core.story.application.query.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationStreamResponse {

    private String type;

    private NotificationResponse notification;

    private long unreadCount;

    private Instant timestamp;
}
