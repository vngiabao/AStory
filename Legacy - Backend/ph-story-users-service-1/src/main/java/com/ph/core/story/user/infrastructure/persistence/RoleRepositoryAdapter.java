package com.ph.core.story.user.infrastructure.persistence;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.ph.core.story.user.domain.model.Role;
import com.ph.core.story.user.domain.repository.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository jpaRepository;

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRepository.findByName(name);
    }
}
