package com.ph.core.story.application.command;

import com.ph.core.story.application.command.dto.ProfilesCreateRequest;
import com.ph.core.story.application.command.dto.ProfilesUpdateRequest;
import com.ph.core.story.application.mapper.ProfilesMapper;
import com.ph.core.story.application.query.dto.ProfilesResponse;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.common.base.BaseSoftDeleteCrudServiceImpl;
import com.ph.core.story.domain.model.Profiles;
import com.ph.core.story.domain.repository.ProfilesRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProfilesSoftDeleteCrudService extends
        BaseSoftDeleteCrudServiceImpl<
                Profiles,
                ProfilesCreateRequest,
                ProfilesUpdateRequest,
                ProfilesResponse,
                Long> {

    private final ProfilesRepositoryPort repository;
    private final ProfilesMapper mapper;

    @Override
    protected BaseRepositoryPort<Profiles, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<ProfilesCreateRequest, Profiles> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<Profiles, ProfilesUpdateRequest> getUpdateMapper() {
        return mapper::updateEntity;
    }

    @Override
    protected Function<Profiles, ProfilesResponse> getResponseMapper() {
        return mapper::toResponse;
    }

    @Override
    protected Class<Profiles> getEntityClass() {
        return Profiles.class;
    }
}
