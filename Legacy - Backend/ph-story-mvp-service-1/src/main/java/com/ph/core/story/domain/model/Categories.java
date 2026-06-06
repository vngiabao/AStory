package com.ph.core.story.domain.model;

import com.ph.core.story.common.base.BaseEntity;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Categories extends BaseEntity {

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "type_code", length = 50, nullable = false)
    private String typeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "color", length = 20)
    private String color;

}
