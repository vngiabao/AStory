package com.ph.core.story.application.event;

import com.ph.core.story.application.command.NotificationCommandService;
import com.ph.core.story.application.realtime.NotificationRealtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorySharedNotificationListener {

    private final NotificationCommandService notificationService;
    private final NotificationRealtimeService notificationRealtimeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStoryShared(StorySharedEvent event) {
        try {
            notificationService.sendPushForNotification(event.getNotificationId());
            notificationRealtimeService.publishNotificationCreated(event.getNotificationId());
        } catch (Exception ex) {
            log.warn("Dispatch story shared notification failed for notification {}", event.getNotificationId(), ex);
        }
    }
}
