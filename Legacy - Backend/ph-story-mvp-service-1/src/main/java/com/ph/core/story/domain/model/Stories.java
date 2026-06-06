package com.ph.core.story.domain.model;

import com.ph.core.story.common.base.BaseEntity;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stories")
@Getter
@Setter
public class Stories extends BaseEntity {

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    // @Column(name = "deleted")
    // private Boolean deleted = false;

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profiles profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id")
    private Categories category;
}
