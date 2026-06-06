package com.ph.core.story.user.domain.repository;

import java.util.Optional;
import com.ph.core.story.user.domain.model.Role;

public interface RoleRepositoryPort {
    Optional<Role> findByName(String name);
}
