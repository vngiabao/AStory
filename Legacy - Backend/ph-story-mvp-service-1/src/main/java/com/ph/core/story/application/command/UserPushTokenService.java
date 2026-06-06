package com.ph.core.story.application.command;

import com.ph.core.story.application.command.dto.RegisterPushTokenRequest;
import com.ph.core.story.application.command.dto.UnregisterPushTokenRequest;
import com.ph.core.story.domain.model.UserPushToken;
import com.ph.core.story.infrastructure.UserPushTokenJpaRepository;
import com.ph.core.story.user.domain.model.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPushTokenService {

    private final UserPushTokenJpaRepository repository;
    private final EntityManager entityManager;

    @Transactional
    public void register(Long userId, RegisterPushTokenRequest request) {
        UserPushToken token = repository.findByExpoPushToken(request.getExpoPushToken())
                .orElseGet(UserPushToken::new);

        token.setUser(entityManager.getReference(User.class, userId));
        token.setExpoPushToken(request.getExpoPushToken());
        token.setDeviceId(request.getDeviceId());
        token.setPlatform(request.getPlatform());
        token.setAppVersion(request.getAppVersion());
        token.setEnabled(true);
        token.setDeleted(false);
        token.setLastSeenAt(Instant.now());

        repository.save(token);
        log.info("Registered Expo push token for user {}", userId);
    }

    @Transactional
    public void unregister(Long userId, UnregisterPushTokenRequest request) {
        repository.findByExpoPushToken(request.getExpoPushToken())
                .filter(token -> token.getUser().getId().equals(userId))
                .ifPresent(token -> {
                    token.setEnabled(false);
                    repository.save(token);
                });
    }

    @Transactional
    public void disableToken(String expoPushToken) {
        repository.findByExpoPushToken(expoPushToken)
                .ifPresent(token -> {
                    token.setEnabled(false);
                    repository.save(token);
                });
    }
}
