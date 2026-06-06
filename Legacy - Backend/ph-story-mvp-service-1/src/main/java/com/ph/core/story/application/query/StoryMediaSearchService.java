package com.ph.core.story.application.query;

import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.story.application.fields.StoryMediaFields;
import com.ph.core.story.application.mapper.StoryMediaMapper;
import com.ph.core.story.application.query.dto.StoryMediaResponse;
import com.ph.core.story.common.base.BaseSearchServiceImpl;
import com.ph.core.story.domain.model.StoryMedia;
import com.ph.core.story.domain.repository.StoryMediaRepositoryPort;
import com.ph.core.story.infrastructure.StoryMediaJpaRepository;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class StoryMediaSearchService
        extends BaseSearchServiceImpl<StoryMedia, StoryMediaResponse, Long> {

    private final StoryMediaRepositoryPort repository;
    private final StoryMediaMapper mapper;
    private final QueryExecutor<StoryMedia> queryExecutor;

    public StoryMediaSearchService(StoryMediaRepositoryPort repository,
            StoryMediaJpaRepository jpaRepository, StoryMediaMapper mapper,
            QuerySecurityConfig securityConfig) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryExecutor =
                buildExecutor(jpaRepository, StoryMediaFields.FILTER_FIELDS, securityConfig);
    }

    @Override
    protected StoryMediaRepositoryPort getRepository() {
        return repository;
    }

    @Override
    protected Function<StoryMedia, StoryMediaResponse> getMapper() {
        return mapper::toResponse;
    }

    @Override
    protected QueryExecutor<StoryMedia> getQueryExecutor() {
        return queryExecutor;
    }
}
