package com.ph.core.story.domain.model;

import com.ph.core.story.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "story_media",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_story_media", columnNames = {"story_id", "media_id"})
        }
)
@Getter
@Setter
public class StoryMedia extends BaseEntity {

    @Column(name = "caption", columnDefinition = "text")
    private String caption;

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Stories story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private MediaFiles media;
}
