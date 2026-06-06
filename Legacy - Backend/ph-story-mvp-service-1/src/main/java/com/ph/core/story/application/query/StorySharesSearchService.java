package com.ph.core.story.application.query;

import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.story.application.fields.StorySharesFields;
import com.ph.core.story.application.mapper.StorySharesMapper;
import com.ph.core.story.application.query.dto.StorySharedHistoryItemResponse;
import com.ph.core.story.application.query.dto.StorySharedItemResponse;
import com.ph.core.story.application.query.dto.StorySharesResponse;
import com.ph.core.story.common.base.BaseSearchServiceImpl;
import com.ph.core.story.domain.model.StoryShares;
import com.ph.core.story.domain.repository.StorySharesRepositoryPort;
import com.ph.core.story.infrastructure.StorySharesJpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class StorySharesSearchService
        extends BaseSearchServiceImpl<StoryShares, StorySharesResponse, Long> {

    private final StorySharesRepositoryPort repository;
    private final StorySharesMapper mapper;
    private final QueryExecutor<StoryShares> queryExecutor;

    public StorySharesSearchService(StorySharesRepositoryPort repository,
            StorySharesJpaRepository jpaRepository, StorySharesMapper mapper,
            QuerySecurityConfig securityConfig) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryExecutor = buildExecutor(jpaRepository, StorySharesFields.FILTER_FIELDS, securityConfig);
    }

    @Override
    protected StorySharesRepositoryPort getRepository() {
        return repository;
    }

    @Override
    protected Function<StoryShares, StorySharesResponse> getMapper() {
        return mapper::toResponse;
    }

    @Override
    protected QueryExecutor<StoryShares> getQueryExecutor() {
        return queryExecutor;
    }

    public Page<StorySharedItemResponse> getReceivedStories(Long userId, String keyword, Pageable pageable) {
        return repository.findReceivedStories(userId, keyword, pageable);
    }

    public Page<StorySharedHistoryItemResponse> getSharedHistory(Long userId, String keyword, Pageable pageable) {
        return repository.findSharedHistory(userId, keyword, pageable);
    }
}
