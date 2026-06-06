package com.ph.core.story.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.ph.core.story.common.base.BaseEntity;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "profiles")
@Getter
@Setter
public class Profiles extends BaseEntity {

    @Column(name = "fullname", length = 150)
    private String fullname;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "legacy_settings", columnDefinition = "jsonb")
    private JsonNode legacySettings;

    @Column(name = "is_deceased")
    private Boolean isDeceased;

    @Column(name = "memorial_message", length = 500)
    private String memorialMessage;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth")
    private Instant dateOfBirth;

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    private MediaFiles avatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legacy_user_id")
    private User legacyUser;
}
