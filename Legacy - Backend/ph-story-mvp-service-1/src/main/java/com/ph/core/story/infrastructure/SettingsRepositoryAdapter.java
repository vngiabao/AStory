package com.ph.core.story.infrastructure;

import com.ph.core.story.common.base.BaseRepositoryAdapter;
import com.ph.core.story.domain.model.Settings;
import com.ph.core.story.domain.repository.SettingsRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SettingsRepositoryAdapter extends BaseRepositoryAdapter<Settings, Long>
        implements SettingsRepositoryPort {

    private final SettingsJpaRepository repository;

    public SettingsRepositoryAdapter(SettingsJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<Settings> findById(Long id) {
        return repository.findById(id).filter(settings -> !settings.isDeleted());
    }

    @Override
    public Page<Settings> findAll(Pageable pageable) {
        return repository.findAll(
                (root, query, cb) -> cb.isFalse(root.get("deleted")),
                pageable
        );
    }
}
