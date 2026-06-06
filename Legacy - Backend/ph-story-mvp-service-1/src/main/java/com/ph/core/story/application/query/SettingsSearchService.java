package com.ph.core.story.application.query;

import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.story.application.fields.SettingsFields;
import com.ph.core.story.application.mapper.SettingsMapper;
import com.ph.core.story.application.query.dto.SettingsResponse;
import com.ph.core.story.common.base.BaseSearchServiceImpl;
import com.ph.core.story.domain.model.Settings;
import com.ph.core.story.domain.repository.SettingsRepositoryPort;
import com.ph.core.story.infrastructure.SettingsJpaRepository;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class SettingsSearchService extends BaseSearchServiceImpl<Settings, SettingsResponse, Long> {

    private final SettingsRepositoryPort repository;
    private final SettingsMapper mapper;
    private final QueryExecutor<Settings> queryExecutor;

    public SettingsSearchService(SettingsRepositoryPort repository,
            SettingsJpaRepository jpaRepository, SettingsMapper mapper,
            QuerySecurityConfig securityConfig) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryExecutor =
                buildExecutor(jpaRepository, SettingsFields.FILTER_FIELDS, securityConfig);
    }

    @Override
    protected SettingsRepositoryPort getRepository() {
        return repository;
    }

    @Override
    protected Function<Settings, SettingsResponse> getMapper() {
        return mapper::toResponse;
    }

    @Override
    protected QueryExecutor<Settings> getQueryExecutor() {
        return queryExecutor;
    }
}
