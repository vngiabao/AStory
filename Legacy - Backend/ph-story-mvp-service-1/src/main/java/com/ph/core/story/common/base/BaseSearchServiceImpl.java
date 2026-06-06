package com.ph.core.story.common.base;

import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;

/**
 * Base implementation for search services providing reusable logic for entity retrieval and dynamic
 * queries.
 *
 * <p>
 * This class centralizes common search functionality so that entity-specific services only need to
 * provide:
 * <ul>
 * <li>A repository implementation</li>
 * <li>An entity-to-response mapper</li>
 * <li>A configured {@link QueryExecutor}</li>
 * </ul>
 *
 * <p>
 * The {@code search()} method delegates dynamic filtering, sorting and pagination to
 * {@link QueryExecutor}.
 *
 * <p>
 * Entities marked as deleted (soft delete) will not be returned by {@code findById}.
 *
 * <p>
 * <b>Important:</b> The repository used by {@link QueryExecutor} must implement
 * {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor}.
 *
 * @param <E> entity type extending {@link BaseEntity}
 * @param <R> response DTO returned by the service
 * @param <ID> identifier type
 *
 * @see BaseSearchService
 * @see QueryExecutor
 *
 * @since 1.0
 */
@Transactional(readOnly = true)
public abstract class BaseSearchServiceImpl<E extends BaseEntity, R, ID extends Serializable>
        implements BaseSearchService<R, ID> {

    protected abstract BaseRepositoryPort<E, ID> getRepository();

    protected abstract Function<E, R> getMapper();

    protected abstract QueryExecutor<E> getQueryExecutor();

    protected QueryExecutor<E> buildExecutor(
            // Repository must extend JpaSpecificationExecutor to support dynamic queries
            JpaSpecificationExecutor<E> repository,
            // Allowed fields for filtering and sorting
            Set<String> allowedFields,
            // Security config for query execution
            QuerySecurityConfig securityConfig) {

        return new QueryExecutor<>(repository, allowedFields, securityConfig);
    }

    @Override
    public R findById(ID id) {
        return getRepository()
                // Ensure the entity is not deleted before mapping to response
                .findById(id).filter(entity -> !entity.isDeleted()).map(getMapper())
                // If not found or deleted, throw a ResourceNotFoundException
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));
    }

    @Override
    public Page<R> findAll(Pageable pageable) {
        return getRepository().findAll(pageable).map(getMapper());
    }

    @Override
    public Page<R> search(QueryRequest request) {

        return getQueryExecutor().execute(request).map(getMapper());
    }
}
