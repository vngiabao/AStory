package com.ph.core.story.application.command;

import com.ph.core.story.application.command.dto.StoriesCreateRequest;
import com.ph.core.story.application.command.dto.StoriesUpdateRequest;
import com.ph.core.story.application.mapper.StoriesMapper;
import com.ph.core.story.application.query.dto.StoriesResponse;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.common.base.BaseSoftDeleteCrudServiceImpl;
import com.ph.core.story.common.exception.BusinessValidationException;
import com.ph.core.story.domain.model.Stories;
import com.ph.core.story.domain.repository.StoriesRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class StoriesSoftDeleteCrudService extends
        BaseSoftDeleteCrudServiceImpl<Stories, StoriesCreateRequest, StoriesUpdateRequest, StoriesResponse, Long> {

    private final StoriesRepositoryPort repository;
    private final StoriesMapper mapper;

    @Override
    protected BaseRepositoryPort<Stories, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<StoriesCreateRequest, Stories> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<Stories, StoriesUpdateRequest> getUpdateMapper() {
        return mapper::updateEntity;
    }

    @Override
    protected Function<Stories, StoriesResponse> getResponseMapper() {
        return mapper::toResponse;
    }

    @Override
    public StoriesResponse update(Long id, StoriesUpdateRequest request) {
        if (Boolean.TRUE.equals(request.getClearCategory()) && request.getCatId() != null) {
            throw new BusinessValidationException(
                    "Cannot provide categoryId when clearCategory is true",
                    "Send categoryId to change category, or clearCategory=true to remove it.");
        }
        return super.update(id, request);
    }

    @Override
    protected Class<Stories> getEntityClass() {
        return Stories.class;
    }
}
