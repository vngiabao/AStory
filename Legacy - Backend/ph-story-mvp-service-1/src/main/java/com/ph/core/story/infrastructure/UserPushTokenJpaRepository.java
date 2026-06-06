package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.UserPushToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPushTokenJpaRepository extends JpaRepository<UserPushToken, Long> {

    Optional<UserPushToken> findByExpoPushToken(String expoPushToken);

    List<UserPushToken> findByUserIdAndEnabledTrueAndDeletedFalse(Long userId);
}
