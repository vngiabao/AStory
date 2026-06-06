package com.ph.core.story.domain.model;

import com.ph.core.story.common.base.BaseEntity;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "story_shares",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_story_share", columnNames = {"story_id", "shared_user_id"})
        }
)
@Getter
@Setter
public class StoryShares extends BaseEntity {

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Stories story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_user_id", nullable = false)
    private User sharedUser;
}
