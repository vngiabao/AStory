package com.ph.core.story.application.query;

import com.ph.core.story.application.fields.CategoriesFields;
import com.ph.core.story.application.mapper.CategoriesMapper;
import org.springframework.stereotype.Service;
import com.ph.core.story.common.base.BaseSearchServiceImpl;
import com.ph.core.story.domain.model.Categories;
import com.ph.core.story.domain.repository.CategoriesRepositoryPort;
import com.ph.core.story.infrastructure.CategoriesJpaRepository;
import com.ph.core.story.application.query.dto.CategoriesResponse;
import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;

import java.util.function.Function;

/**
 * Search service responsible for retrieving and searching {@link CategoriesEntity}.
 *
 * <p>
 * This service extends {@link BaseSearchServiceImpl} and provides the required dependencies for
 * executing search operations in the categories domain.
 *
 * <p>
 * The service configures a {@link QueryExecutor} with:
 * <ul>
 * <li>{@link CategoriesJpaRepository} for database access</li>
 * <li>{@link CategoriesFields#FILTER_FIELDS} defining allowed filter fields</li>
 * <li>{@link QuerySecurityConfig} enforcing query security rules</li>
 * </ul>
 *
 * <p>
 * Dynamic search requests are handled through {@link QueryRequest}, allowing flexible filtering,
 * sorting, and pagination while ensuring only allowed fields can be queried.
 *
 * <p>
 * This class is typically used by the Categories REST controller to provide search capabilities for
 * category resources.
 *
 * @see BaseSearchServiceImpl
 * @see CategoriesFields
 * @see QueryExecutor
 * @see QueryRequest
 *
 * @since 1.0
 */
@Service
public class CategoriesSearchService
        extends BaseSearchServiceImpl<Categories, CategoriesResponse, Long> {

    private final CategoriesRepositoryPort repository;
    private final CategoriesMapper mapper;
    private final QueryExecutor<Categories> queryExecutor;

    public CategoriesSearchService(CategoriesRepositoryPort repository,
            CategoriesJpaRepository jpaRepository, CategoriesMapper mapper,
            QuerySecurityConfig securityConfig) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryExecutor =
                buildExecutor(jpaRepository, CategoriesFields.FILTER_FIELDS, securityConfig);
    }

    @Override
    protected CategoriesRepositoryPort getRepository() {
        return repository;
    }

    @Override
    protected Function<Categories, CategoriesResponse> getMapper() {
        return mapper::toResponse;
    }

    @Override
    protected QueryExecutor<Categories> getQueryExecutor() {
        return queryExecutor;
    }
}
