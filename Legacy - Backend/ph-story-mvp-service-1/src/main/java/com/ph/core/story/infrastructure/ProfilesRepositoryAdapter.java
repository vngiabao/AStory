package com.ph.core.story.infrastructure;

import com.ph.core.story.common.base.BaseRepositoryAdapter;
import com.ph.core.story.domain.model.Profiles;
import com.ph.core.story.domain.repository.ProfilesRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProfilesRepositoryAdapter extends BaseRepositoryAdapter<Profiles, Long>
        implements ProfilesRepositoryPort {

    private final ProfilesJpaRepository repository;

    public ProfilesRepositoryAdapter(ProfilesJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<Profiles> findById(Long id) {
        return repository.findById(id).filter(profile -> !profile.isDeleted());
    }

    @Override
    public Page<Profiles> findAll(Pageable pageable) {
        return repository.findAll(
                (root, query, cb) -> cb.isFalse(root.get("deleted")),
                pageable);
    }

    @Override
    public Profiles findByUserId(Long userId) {
        return repository.findByUserIdAndDeletedFalse(userId);
    }
}
