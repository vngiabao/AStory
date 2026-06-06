package com.ph.core.story.security;

import java.io.IOException;
import java.time.Instant;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.ph.core.story.configuration.SecurityServerProperties;
import com.ph.core.story.repositories.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final UsersRepository userRepository;
    private final SecurityServerProperties securityServerProperties;

    @Override
    @Transactional
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        String username = request.getParameter("username");

        if (username != null) {

            userRepository.findByUsernameIgnoreCase(username).ifPresent(user -> {

                // Nếu account đã lock rồi → không không cần xử lý gì nữa
                if (!user.isAccountNonLocked()) {
                    return;
                }

                int attempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(attempts);

                if (attempts >= securityServerProperties.getLogin().getMaxAttempts()) {
                    user.setAccountNonLocked(false);
                    user.setLockTime(Instant.now());
                }

                userRepository.save(user);
            });
        }

        response.sendRedirect("/login?error");
    }
}
