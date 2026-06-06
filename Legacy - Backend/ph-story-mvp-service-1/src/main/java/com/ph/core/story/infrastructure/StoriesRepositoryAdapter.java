package com.ph.core.story.infrastructure;

import com.ph.core.story.common.base.BaseRepositoryAdapter;
import com.ph.core.story.domain.model.Stories;
import com.ph.core.story.domain.repository.StoriesRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StoriesRepositoryAdapter extends BaseRepositoryAdapter<Stories, Long>
        implements StoriesRepositoryPort {

    private final StoriesJpaRepository repository;

    public StoriesRepositoryAdapter(StoriesJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<Stories> findById(Long id) {
        return repository.findById(id).filter(story -> !story.isDeleted());
    }

    @Override
    public Page<Stories> findAll(Pageable pageable) {
        return repository.findAll(
                (root, query, cb) -> cb.isFalse(root.get("deleted")),
                pageable
        );
    }

    @Override
    public Page<Stories> findAllByUserId(Long userId, Pageable pageable) {
        Specification<Stories> spec = (root, query, cb) -> cb.and(
                cb.isFalse(root.get("deleted")),
                cb.equal(root.get("user").get("id"), userId)
        );
        return repository.findAll(spec, pageable);
    }

    @Override
    public void clearCategoryByCategoryId(Long categoryId) {
        repository.clearCategoryByCategoryId(categoryId);
    }
}
