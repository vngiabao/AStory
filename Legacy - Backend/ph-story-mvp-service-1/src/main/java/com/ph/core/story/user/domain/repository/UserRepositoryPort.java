package com.ph.core.story.user.domain.repository;

import java.util.Optional;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.user.domain.model.User;

public interface UserRepositoryPort extends BaseRepositoryPort<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User getReferenceIfExists(Long id);
}
