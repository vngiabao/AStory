package com.ph.core.story.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.ph.core.story.common.base.BaseEntity;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "settings")
@Getter
@Setter
public class Settings extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "general", columnDefinition = "jsonb")
    private JsonNode general;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile", columnDefinition = "jsonb")
    private JsonNode profile;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "story", columnDefinition = "jsonb")
    private JsonNode story;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "media_file", columnDefinition = "jsonb")
    private JsonNode mediaFile;

    @Column(name = "deleted")
    private Boolean deleted = false;

    // ===== RELATION =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
