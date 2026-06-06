package com.ph.core.story.user.application.query;

import com.ph.core.story.user.application.fields.UserFields;
import com.ph.core.story.user.application.mapper.UserMapper;
import com.ph.core.story.user.application.query.dto.UserResponse;
import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.common.base.BaseSearchServiceImpl;
import com.ph.core.story.user.domain.model.User;
import com.ph.core.story.user.domain.repository.UserRepositoryPort;
import com.ph.core.story.user.infrastructure.persistence.UserJpaRepository;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserSearchService extends BaseSearchServiceImpl<User, UserResponse, Long> {

    private final UserRepositoryPort repository;
    private final UserMapper mapper;
    private final QueryExecutor<User> queryExecutor;

    public UserSearchService(UserRepositoryPort repository, UserJpaRepository jpaRepository,
            UserMapper mapper, QuerySecurityConfig securityConfig) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryExecutor =
                new QueryExecutor<>(jpaRepository, UserFields.FILTER_FIELDS, securityConfig);
    }

    @Override
    protected BaseRepositoryPort<User, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<User, UserResponse> getMapper() {
        return mapper::toResponse;
    }

    @Override
    protected QueryExecutor<User> getQueryExecutor() {
        return queryExecutor;
    }
}


