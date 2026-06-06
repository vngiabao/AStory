package com.ph.core.story.user.domain.model;

import com.ph.core.story.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
// Luôn thêm điều kiện "where deleted = false" -> tức là chỉ lấy giá trị chưa soft delete
@SQLRestriction("deleted = false")
public class User extends BaseEntity {

    @Column(nullable = false, length = 40)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 80)
    private String email;

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
}

