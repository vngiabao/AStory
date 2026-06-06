package com.ph.core.story.models;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
// Luôn thêm điều kiện "where deleted = false" -> tức là chỉ lấygiá trị chưa soft delete
@SQLRestriction("deleted = false")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 80)
    private String email;

    /**
     * Cho phép client biết user đã xác thực email hay chưa.
     */
    @Column(nullable = false)
    private boolean emailVerified = false;

    private String emailVerificationOtpHash;

    private Instant emailVerificationOtpExpiresAt;

    private Instant emailVerificationOtpSentAt;

    @Column(nullable = false)
    private int emailVerificationFailedAttempts = 0;

    /**
     * Trạng thái tài khoản
     */
    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    /**
     * Loại người dùng: SENIOR, FAMILY
     */
    @Enumerated(EnumType.STRING)
    private UserType userType;

    /**
     * Bật xác thực 2 yếu tố - mặc định không (false)
     */
    @Column(nullable = false)
    private boolean twoFactorEnabled = false;

    /**
     * Số lần đăng nhập không thành công liên tiếp
     */
    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    /**
     * Thời điểm lock
     */
    private Instant lockTime;

    /**
     * Quan hệ roles
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * ===================================================
     * 
     * Audit fields
     * 
     * ===================================================
     */

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
