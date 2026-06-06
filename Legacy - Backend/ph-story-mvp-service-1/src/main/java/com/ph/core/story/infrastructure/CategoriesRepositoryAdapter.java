package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.Categories;
import com.ph.core.story.domain.repository.CategoriesRepositoryPort;
import com.ph.core.story.common.base.BaseRepositoryAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CategoriesRepositoryAdapter extends BaseRepositoryAdapter<Categories, Long> implements CategoriesRepositoryPort {

    private final CategoriesJpaRepository repository;

    public CategoriesRepositoryAdapter(CategoriesJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<Categories> findById(Long id) {
        return repository.findById(id).filter(cat -> !cat.isDeleted());
    }

    @Override
    public Page<Categories> findAll(Pageable pageable) {
        return repository.findAll(((root, query, cb) -> cb.isFalse(root.get("deleted"))), pageable);
    }
}
