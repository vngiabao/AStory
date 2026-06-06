package com.ph.core.story.common.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents the repository port used by the domain/application layer.
 *
 * <p>
 * This abstraction decouples the service layer from the underlying persistence technology (e.g.
 * JPA, JDBC).
 *
 * <p>
 * Concrete implementations typically delegate to a Spring Data JPA repository.
 *
 * @param <E> entity type
 * @param <ID> identifier type
 *
 * @since 1.0
 */
public interface BaseRepositoryPort<E, ID extends Serializable> {

    E save(E entity);

    Optional<E> findById(ID id);

    Page<E> findAll(Pageable pageable);

    /**
     * Dùng cho BaseHardDeleteCrudServiceImpl
     *
     * @param id
     */
    void deleteById(ID id);
}

