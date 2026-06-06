package com.ph.core.story.infrastructure;

import com.ph.core.story.common.base.BaseRepositoryAdapter;
import com.ph.core.story.domain.model.MediaFiles;
import com.ph.core.story.domain.repository.MediaFilesRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MediaFilesRepositoryAdapter extends BaseRepositoryAdapter<MediaFiles, Long>
        implements MediaFilesRepositoryPort {

    private final MediaFilesJpaRepository repository;

    public MediaFilesRepositoryAdapter(MediaFilesJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<MediaFiles> findById(Long id) {
        return repository.findById(id).filter(media -> !media.isDeleted());
    }

    @Override
    public Page<MediaFiles> findAll(Pageable pageable) {
        return repository.findAll(
                (root, query, cb) -> cb.isFalse(root.get("deleted")),
                pageable
        );
    }

    @Override
    public void clearCategoryByCategoryId(Long categoryId) {
        repository.clearCategoryByCategoryId(categoryId);
    }
}
