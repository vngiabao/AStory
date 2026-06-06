package com.ph.core.story.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.ph.core.story.common.base.BaseEntity;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    private User recipientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id")
    private User actorUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "body", nullable = false, length = 500)
    private String body;

    @Column(name = "story_id")
    private Long storyId;

    @Column(name = "story_share_id")
    private Long storyShareId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private JsonNode data;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "opened_at")
    private Instant openedAt;

    @Column(name = "push_sent_at")
    private Instant pushSentAt;

    @Column(name = "push_error", length = 1000)
    private String pushError;
}
