package com.ph.core.story.user.application.command;

import com.ph.core.story.common.base.BaseSoftDeleteCrudServiceImpl;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.user.application.command.dto.UserCreateRequest;
import com.ph.core.story.user.application.command.dto.UserUpdateRequest;
import com.ph.core.story.user.application.mapper.UserMapper;
import com.ph.core.story.user.application.query.dto.UserResponse;
import com.ph.core.story.user.domain.model.User;
import com.ph.core.story.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserSoftDeleteCrudService extends
        BaseSoftDeleteCrudServiceImpl<User, UserCreateRequest, UserUpdateRequest, UserResponse, Long> {

    private final UserRepositoryPort repository;
    private final UserMapper mapper;

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected BaseRepositoryPort<User, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<UserCreateRequest, User> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<User, UserUpdateRequest> getUpdateMapper() {
        return mapper::update;
    }

    @Override
    protected Function<User, UserResponse> getResponseMapper() {
        return mapper::toResponse;
    }
}

