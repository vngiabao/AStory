package com.ph.core.story.application.query;

import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.story.application.fields.MediaFilesFields;
import com.ph.core.story.application.mapper.MediaFilesMapper;
import com.ph.core.story.application.query.dto.MediaFilesResponse;
import com.ph.core.story.common.base.BaseSearchServiceImpl;
import com.ph.core.story.domain.model.MediaFiles;
import com.ph.core.story.domain.repository.MediaFilesRepositoryPort;
import com.ph.core.story.infrastructure.MediaFilesJpaRepository;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class MediaFilesSearchService
        extends BaseSearchServiceImpl<MediaFiles, MediaFilesResponse, Long> {

    private final MediaFilesRepositoryPort repository;
    private final MediaFilesMapper mapper;
    private final QueryExecutor<MediaFiles> queryExecutor;

    public MediaFilesSearchService(MediaFilesRepositoryPort repository,
            MediaFilesJpaRepository jpaRepository, MediaFilesMapper mapper,
            QuerySecurityConfig securityConfig) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryExecutor =
                buildExecutor(jpaRepository, MediaFilesFields.FILTER_FIELDS, securityConfig);
    }

    @Override
    protected MediaFilesRepositoryPort getRepository() {
        return repository;
    }

    @Override
    protected Function<MediaFiles, MediaFilesResponse> getMapper() {
        return mapper::toResponse;
    }

    @Override
    protected QueryExecutor<MediaFiles> getQueryExecutor() {
        return queryExecutor;
    }
}
