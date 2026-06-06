package com.ph.core.story.user.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ph.core.story.user.domain.model.Role;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
