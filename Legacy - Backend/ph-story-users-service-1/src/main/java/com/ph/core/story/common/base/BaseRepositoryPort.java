package com.ph.core.story.common.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Optional;

public interface BaseRepositoryPort<E, ID extends Serializable> {

    E save(E entity);

    Optional<E> findById(ID id);

    Page<E> findAll(Pageable pageable);
}

