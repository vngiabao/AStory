package com.ph.core.story.security;

import com.ph.core.story.models.Users;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private final String userType;

    public CustomUserDetails(Users user) {
        super(
                // Tên đăng nhập
                user.getUsername(),
                // Mật khẩu hash
                user.getPasswordHash(),
                // Enabled
                user.isEnabled(),
                // accountNonExpired
                true,
                // credentialsNonExpired
                true,
                // Trạng thái khoá tài khoản
                user.isAccountNonLocked(),
                // Danh sách roles
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList());
        // Loại người dùng
        this.userType = user.getUserType().name();
    }

    public String getUserType() {
        return userType;
    }
}
