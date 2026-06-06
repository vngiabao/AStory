package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.Notification;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientUserIdAndDeletedFalseOrderByCreatedDateDesc(Long recipientUserId, Pageable pageable);

    Optional<Notification> findByIdAndRecipientUserIdAndDeletedFalse(Long id, Long recipientUserId);

    long countByRecipientUserIdAndReadAtIsNullAndDeletedFalse(Long recipientUserId);

    @Modifying
    @Query("""
            UPDATE Notification n
               SET n.readAt = :readAt
             WHERE n.recipientUser.id = :recipientUserId
               AND n.readAt IS NULL
               AND n.deleted = false
            """)
    int markAllRead(@Param("recipientUserId") Long recipientUserId, @Param("readAt") Instant readAt);
}
