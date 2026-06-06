package com.ph.core.story.security;

import com.ph.core.story.configuration.SecurityServerProperties;
import com.ph.core.story.models.Users;
import com.ph.core.story.repositories.UsersRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository repository;
    private final SecurityServerProperties securityServerProperties;

    public CustomUserDetailsService(UsersRepository repository,
            SecurityServerProperties securityServerProperties) {
        this.repository = repository;
        this.securityServerProperties = securityServerProperties;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = repository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isAccountNonLocked()) {
            unlockIfExpired(user);
        }

        return buildUserDetails(user);
    }

    private void unlockIfExpired(Users user) {

        if (user.getLockTime() == null) {
            return;
        }

        Instant unlockTime = user.getLockTime().plus(
                securityServerProperties.getLogin().getLockDurationMinutes(), ChronoUnit.MINUTES);

        if (Instant.now().isAfter(unlockTime)) {

            user.setAccountNonLocked(true);
            user.setFailedLoginAttempts(0);
            user.setLockTime(null);

            repository.save(user);
        }
    }

    private UserDetails buildUserDetails(Users user) {

        return org.springframework.security.core.userdetails.User.builder()
                // Tên đăng nhập
                .username(user.getUsername())
                // Mật khẩu hash
                .password(user.getPasswordHash())
                // Bị disable không
                .disabled(!user.isEnabled())
                // Trạng thái tài khoản (khoá/hoạt động)
                .accountLocked(!user.isAccountNonLocked())
                // Danh sách ROLES
                .authorities(
                        user.getRoles().stream().map(role -> role.getName()).toArray(String[]::new))
                .build();
    }
}

