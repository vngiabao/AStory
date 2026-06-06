package com.ph.core.story.application.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ph.core.story.application.realtime.NotificationRealtimeService;
import com.ph.core.story.application.query.dto.NotificationResponse;
import com.ph.core.story.common.exception.ResourceNotFoundException;
import com.ph.core.story.domain.model.Notification;
import com.ph.core.story.domain.model.NotificationType;
import com.ph.core.story.domain.model.Stories;
import com.ph.core.story.domain.model.StoryShares;
import com.ph.core.story.domain.model.UserPushToken;
import com.ph.core.story.domain.repository.StorySharesRepositoryPort;
import com.ph.core.story.infrastructure.NotificationJpaRepository;
import com.ph.core.story.infrastructure.NotificationQueryRepository;
import com.ph.core.story.infrastructure.UserPushTokenJpaRepository;
import com.ph.core.story.infrastructure.push.ExpoPushClient;
import com.ph.core.story.infrastructure.push.ExpoPushClient.ExpoPushMessage;
import com.ph.core.story.infrastructure.push.ExpoPushClient.ExpoPushResult;
import com.ph.core.story.user.domain.model.User;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCommandService {

    private final NotificationJpaRepository notificationRepository;
    private final NotificationQueryRepository queryRepository;
    private final UserPushTokenJpaRepository pushTokenRepository;
    private final StorySharesRepositoryPort storySharesRepository;
    private final ExpoPushClient expoPushClient;
    private final UserPushTokenService userPushTokenService;
    private final NotificationRealtimeService notificationRealtimeService;
    private final ObjectMapper objectMapper;

    @Transactional
    public NotificationResponse markRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository
                .findByIdAndRecipientUserIdAndDeletedFalse(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));

        Instant now = Instant.now();
        if (notification.getReadAt() == null) {
            notification.setReadAt(now);
        }
        notification.setOpenedAt(now);

        NotificationResponse response = queryRepository.toResponse(notificationRepository.save(notification));
        notificationRealtimeService.publishNotificationRead(userId, response);
        return response;
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllRead(userId, Instant.now());
        notificationRealtimeService.publishAllRead(userId);
    }

    @Transactional
    public Long createStorySharedNotification(Long storyShareId) {
        StoryShares storyShare = storySharesRepository.findById(storyShareId)
                .orElseThrow(() -> new ResourceNotFoundException("StoryShares", storyShareId));

        Stories story = storyShare.getStory();
        User recipient = storyShare.getSharedUser();
        User actor = story.getUser();

        String actorName = resolveActorName(story);
        String storyTitle = story.getTitle() == null || story.getTitle().isBlank()
                ? "Một story mới"
                : story.getTitle();

        Map<String, Object> data = new HashMap<>();
        data.put("type", NotificationType.STORY_SHARED.name());
        data.put("storyId", story.getId());
        data.put("storyShareId", storyShare.getId());
        data.put("notificationType", NotificationType.STORY_SHARED.name());

        Notification notification = new Notification();
        notification.setRecipientUser(recipient);
        notification.setActorUser(actor);
        notification.setType(NotificationType.STORY_SHARED);
        notification.setTitle(actorName + " đã chia sẻ story với bạn");
        notification.setBody(storyTitle);
        notification.setStoryId(story.getId());
        notification.setStoryShareId(storyShare.getId());
        notification.setData(objectMapper.valueToTree(data));

        Notification saved = notificationRepository.save(notification);
        data.put("notificationId", saved.getId());
        saved.setData(objectMapper.valueToTree(data));
        saved = notificationRepository.save(saved);

        log.info("Created story shared notification {} for story share {}", saved.getId(), storyShareId);
        return saved.getId();
    }

    @Transactional
    public void sendPushForNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));

        if (notification.getStoryId() == null || notification.getRecipientUser() == null) {
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("type", notification.getType().name());
        data.put("notificationType", notification.getType().name());
        data.put("notificationId", notification.getId());
        data.put("storyId", notification.getStoryId());
        data.put("storyShareId", notification.getStoryShareId());

        sendPushToUser(notification.getRecipientUser().getId(), notification, data);
    }

    private void sendPushToUser(Long userId, Notification notification, Map<String, Object> data) {
        var tokens = pushTokenRepository.findByUserIdAndEnabledTrueAndDeletedFalse(userId);
        if (tokens.isEmpty()) {
            return;
        }

        Instant pushSentAt = null;
        String pushError = null;

        for (UserPushToken token : tokens) {
            ExpoPushResult result = expoPushClient.send(ExpoPushMessage.builder()
                    .to(token.getExpoPushToken())
                    .title(notification.getTitle())
                    .body(notification.getBody())
                    .data(data)
                    .build());

            if (result.isSuccess()) {
                pushSentAt = result.getSentAt();
            } else {
                pushError = result.getError();
                if ("DeviceNotRegistered".equals(result.getErrorCode())) {
                    userPushTokenService.disableToken(token.getExpoPushToken());
                }
            }
        }

        notification.setPushSentAt(pushSentAt);
        notification.setPushError(pushError);
        notificationRepository.save(notification);
    }

    private String resolveActorName(Stories story) {
        if (story.getProfile() != null
                && story.getProfile().getFullname() != null
                && !story.getProfile().getFullname().isBlank()) {
            return story.getProfile().getFullname();
        }

        if (story.getUser() != null && story.getUser().getUsername() != null) {
            return story.getUser().getUsername();
        }

        return "Ai đó";
    }
}
