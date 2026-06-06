package com.ph.core.story.common.base;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseRepositoryAdapter<E, ID extends Serializable>
        implements BaseRepositoryPort<E, ID> {

    protected final JpaRepository<E, ID> repository;

    @Override
    public E save(E entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<E> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }
}
