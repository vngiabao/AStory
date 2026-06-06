package com.ph.core.story.domain.model;

import com.ph.core.story.common.base.BaseEntity;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "user_push_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_push_token", columnNames = "expo_push_token")
        })
@Getter
@Setter
public class UserPushToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expo_push_token", nullable = false, length = 255)
    private String expoPushToken;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Column(name = "platform", length = 30)
    private String platform;

    @Column(name = "app_version", length = 50)
    private String appVersion;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt = Instant.now();
}
