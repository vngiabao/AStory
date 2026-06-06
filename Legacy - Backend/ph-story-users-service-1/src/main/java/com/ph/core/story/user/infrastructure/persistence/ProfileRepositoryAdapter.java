package com.ph.core.story.user.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import com.ph.core.story.user.domain.model.Profiles;
import com.ph.core.story.user.domain.repository.ProfileRepositoryPort;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryAdapter implements ProfileRepositoryPort {

    private final ProfileJpaRepository repository;

    @Override
    public Profiles save(Profiles profile) {
        return repository.save(profile);
    }
}
