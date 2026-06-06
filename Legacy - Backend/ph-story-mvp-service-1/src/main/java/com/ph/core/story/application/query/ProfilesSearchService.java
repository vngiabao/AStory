package com.ph.core.story.application.query;

import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.story.application.fields.ProfilesFields;
import com.ph.core.story.application.mapper.ProfilesMapper;
import com.ph.core.story.application.query.dto.ProfilesResponse;
import com.ph.core.story.application.query.dto.StoriesResponse;
import com.ph.core.story.common.base.BaseSearchServiceImpl;
import com.ph.core.story.domain.model.Profiles;
import com.ph.core.story.domain.repository.ProfilesRepositoryPort;
import com.ph.core.story.infrastructure.ProfilesJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class ProfilesSearchService extends BaseSearchServiceImpl<Profiles, ProfilesResponse, Long> {

    private final ProfilesRepositoryPort repository;
    private final ProfilesMapper mapper;
    private final QueryExecutor<Profiles> queryExecutor;

    public ProfilesSearchService(ProfilesRepositoryPort repository,
            ProfilesJpaRepository jpaRepository, ProfilesMapper mapper,
            QuerySecurityConfig securityConfig) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryExecutor = buildExecutor(jpaRepository, ProfilesFields.FILTER_FIELDS, securityConfig);
    }

    @Override
    protected ProfilesRepositoryPort getRepository() {
        return repository;
    }

    @Override
    protected Function<Profiles, ProfilesResponse> getMapper() {
        return mapper::toResponse;
    }

    @Override
    protected QueryExecutor<Profiles> getQueryExecutor() {
        return queryExecutor;
    }

    /**
     * Lấy Profile user (người đăng nhập). Controller lấy userId từ JWT
     * rồi gọi method này.
     */
    public ProfilesResponse findByUserId(Long userId) {
        Profiles profile = repository.findByUserId(userId);
        return mapper.toResponse(profile);
    }
}
