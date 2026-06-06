package com.ph.core.story.user.domain.model;

import com.ph.core.story.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.SQLRestriction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

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
     * true khi người dùng đã nhập đúng OTP gửi về email.
     * Tài khoản mới đăng ký luôn bắt đầu bằng false.
     */
    @Column(nullable = false)
    private boolean emailVerified = false;

    /**
     * Chỉ lưu hash của OTP, không lưu mã gốc để tránh lộ mã khi DB bị đọc.
     */
    private String emailVerificationOtpHash;

    /**
     * Thời điểm OTP hết hạn.
     */
    private Instant emailVerificationOtpExpiresAt;

    /**
     * Thời điểm gửi OTP gần nhất, dùng để khóa nút gửi lại trong một khoảng ngắn.
     */
    private Instant emailVerificationOtpSentAt;

    /**
     * Số lần nhập sai mã OTP hiện tại.
     */
    @Column(nullable = false)
    private int emailVerificationFailedAttempts = 0;

    /**
     * Chỉ lưu hash OTP dùng cho luồng quên mật khẩu.
     */
    private String passwordResetOtpHash;

    /**
     * Thời điểm OTP quên mật khẩu hết hạn.
     */
    private Instant passwordResetOtpExpiresAt;

    /**
     * Thời điểm gửi OTP quên mật khẩu gần nhất.
     */
    private Instant passwordResetOtpSentAt;

    /**
     * Số lần nhập sai OTP quên mật khẩu hiện tại.
     */
    @Column(nullable = false)
    private int passwordResetFailedAttempts = 0;

    /**
     * Chỉ lưu hash của reset token được cấp sau khi xác thực OTP thành công.
     */
    private String passwordResetTokenHash;

    /**
     * Thời điểm reset token hết hạn.
     */
    private Instant passwordResetTokenExpiresAt;

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

