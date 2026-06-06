package com.ph.core.story.infrastructure;

import com.ph.core.story.application.query.dto.NotificationResponse;
import com.ph.core.story.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {

    private final NotificationJpaRepository repository;

    public Page<NotificationResponse> findForUser(Long userId, Pageable pageable) {
        return repository
                .findByRecipientUserIdAndDeletedFalseOrderByCreatedDateDesc(userId, pageable)
                .map(this::toResponse);
    }

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .body(notification.getBody())
                .storyId(notification.getStoryId())
                .storyShareId(notification.getStoryShareId())
                .actorUserId(notification.getActorUser() == null ? null : notification.getActorUser().getId())
                .read(notification.getReadAt() != null)
                .readAt(notification.getReadAt())
                .openedAt(notification.getOpenedAt())
                .createdDate(notification.getCreatedDate())
                .modifiedDate(notification.getModifiedDate())
                .build();
    }
}
