package com.ph.core.story.common.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Soft delete flag
     */
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * Thời điểm tạo (UTC)
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    /**
     * Người tạo
     */
    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    private String createdBy;

    /**
     * Thời điểm cập nhật cuối
     */
    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedDate;

    /**
     * Người cập nhật cuối
     */
    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy;

    /**
     * Optimistic locking
     */
    @Version
    @Column(nullable = false)
    private Long version;
}


