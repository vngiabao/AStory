package com.ph.core.story.application.realtime;

import com.ph.core.story.application.query.dto.NotificationResponse;
import com.ph.core.story.application.query.dto.NotificationStreamResponse;
import com.ph.core.story.domain.model.Notification;
import com.ph.core.story.infrastructure.NotificationJpaRepository;
import com.ph.core.story.infrastructure.NotificationQueryRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRealtimeService {

    private static final long SSE_TIMEOUT_MILLIS = 0L;
    private static final String EVENT_CONNECTED = "connected";
    private static final String EVENT_NOTIFICATION_CREATED = "notification.created";
    private static final String EVENT_NOTIFICATION_READ = "notification.read";
    private static final String EVENT_NOTIFICATION_READ_ALL = "notification.read-all";
    private static final String EVENT_HEARTBEAT = "heartbeat";

    private final NotificationJpaRepository notificationRepository;
    private final NotificationQueryRepository queryRepository;

    private final Map<Long, Map<String, SseEmitter>> emittersByUserId = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);
        String emitterId = UUID.randomUUID().toString();

        emittersByUserId
                .computeIfAbsent(userId, ignored -> new ConcurrentHashMap<>())
                .put(emitterId, emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitterId));
        emitter.onTimeout(() -> removeEmitter(userId, emitterId));
        emitter.onError(ex -> removeEmitter(userId, emitterId));

        sendEvent(
                userId,
                emitterId,
                emitter,
                EVENT_CONNECTED,
                NotificationStreamResponse.builder()
                        .type(EVENT_CONNECTED)
                        .unreadCount(countUnread(userId))
                        .timestamp(Instant.now())
                        .build(),
                "connected-" + Instant.now().toEpochMilli(),
                true);

        log.info("Opened notification SSE stream for user {}", userId);
        return emitter;
    }

    @Transactional(readOnly = true)
    public void publishNotificationCreated(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification == null || notification.isDeleted()) {
            return;
        }

        Long userId = notification.getRecipientUser().getId();
        NotificationResponse response = queryRepository.toResponse(notification);

        broadcast(
                userId,
                EVENT_NOTIFICATION_CREATED,
                NotificationStreamResponse.builder()
                        .type(EVENT_NOTIFICATION_CREATED)
                        .notification(response)
                        .unreadCount(countUnread(userId))
                        .timestamp(Instant.now())
                        .build(),
                String.valueOf(notificationId));
    }

    public void publishNotificationRead(Long userId, NotificationResponse notification) {
        broadcast(
                userId,
                EVENT_NOTIFICATION_READ,
                NotificationStreamResponse.builder()
                        .type(EVENT_NOTIFICATION_READ)
                        .notification(notification)
                        .unreadCount(countUnread(userId))
                        .timestamp(Instant.now())
                        .build(),
                notification.getId() == null ? null : String.valueOf(notification.getId()));
    }

    public void publishAllRead(Long userId) {
        broadcast(
                userId,
                EVENT_NOTIFICATION_READ_ALL,
                NotificationStreamResponse.builder()
                        .type(EVENT_NOTIFICATION_READ_ALL)
                        .unreadCount(countUnread(userId))
                        .timestamp(Instant.now())
                        .build(),
                "all-read-" + Instant.now().toEpochMilli());
    }

    @Scheduled(fixedDelay = 25000L)
    public void sendHeartbeats() {
        emittersByUserId.forEach((userId, emitters) -> emitters.forEach((emitterId, emitter) -> sendEvent(
                userId,
                emitterId,
                emitter,
                EVENT_HEARTBEAT,
                NotificationStreamResponse.builder()
                        .type(EVENT_HEARTBEAT)
                        .unreadCount(countUnread(userId))
                        .timestamp(Instant.now())
                        .build(),
                "heartbeat-" + Instant.now().toEpochMilli(),
                false)));
    }

    private void broadcast(Long userId, String eventName, NotificationStreamResponse payload, String eventId) {
        Map<String, SseEmitter> emitters = emittersByUserId.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        emitters.forEach((emitterId, emitter) -> sendEvent(userId, emitterId, emitter, eventName, payload, eventId, false));
    }

    private void sendEvent(
            Long userId,
            String emitterId,
            SseEmitter emitter,
            String eventName,
            NotificationStreamResponse payload,
            String eventId,
            boolean includeReconnectTime) {
        try {
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                    .name(eventName)
                    .data(payload);

            if (eventId != null) {
                eventBuilder.id(eventId);
            }

            if (includeReconnectTime) {
                eventBuilder.reconnectTime(3000L);
            }

            emitter.send(eventBuilder);
        } catch (IOException ex) {
            removeEmitter(userId, emitterId);
            emitter.completeWithError(ex);
            log.debug("Removed broken notification SSE emitter {} for user {}", emitterId, userId);
        }
    }

    private long countUnread(Long userId) {
        return notificationRepository.countByRecipientUserIdAndReadAtIsNullAndDeletedFalse(userId);
    }

    private void removeEmitter(Long userId, String emitterId) {
        Map<String, SseEmitter> emitters = emittersByUserId.get(userId);
        if (emitters == null) {
            return;
        }

        emitters.remove(emitterId);
        if (emitters.isEmpty()) {
            emittersByUserId.remove(userId);
        }
    }
}
