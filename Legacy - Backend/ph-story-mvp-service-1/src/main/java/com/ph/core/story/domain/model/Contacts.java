package com.ph.core.story.domain.model;

import com.ph.core.story.common.base.BaseEntity;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contacts")
@Getter
@Setter
public class Contacts extends BaseEntity {

    @Column(name = "preference_name", length = 150)
    private String preferenceName;

    // ===== RELATIONS =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profiles profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id")
    private Categories category;
}
