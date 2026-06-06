package com.ph.core.story.domain.model;

import com.ph.core.story.common.base.BaseEntity;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "media_files")
@Getter
@Setter
public class MediaFiles extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", length = 20)
    private MediaType mediaType;

    @Column(name = "url_path")
    private String urlPath;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "title", length = 100)
    private String title;

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id")
    private Categories category;
}
