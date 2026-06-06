package com.ph.core.story.application.query;

import com.ph.core.story.application.query.dto.NotificationResponse;
import com.ph.core.story.application.query.dto.UnreadCountResponse;
import com.ph.core.story.infrastructure.NotificationJpaRepository;
import com.ph.core.story.infrastructure.NotificationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationJpaRepository notificationRepository;
    private final NotificationQueryRepository queryRepository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> findForUser(Long userId, Pageable pageable) {
        return queryRepository.findForUser(userId, pageable);
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse countUnread(Long userId) {
        return new UnreadCountResponse(
                notificationRepository.countByRecipientUserIdAndReadAtIsNullAndDeletedFalse(userId));
    }
}
