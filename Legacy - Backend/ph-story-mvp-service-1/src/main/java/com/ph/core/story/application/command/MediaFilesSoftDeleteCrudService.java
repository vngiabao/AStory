package com.ph.core.story.application.command;

import com.ph.core.story.application.command.dto.MediaFilesCreateRequest;
import com.ph.core.story.application.command.dto.MediaFilesUpdateRequest;
import com.ph.core.story.application.mapper.MediaFilesMapper;
import com.ph.core.story.application.query.dto.MediaFilesResponse;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.common.base.BaseSoftDeleteCrudServiceImpl;
import com.ph.core.story.common.exception.BusinessValidationException;
import com.ph.core.story.domain.model.MediaFiles;
import com.ph.core.story.domain.repository.MediaFilesRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MediaFilesSoftDeleteCrudService extends
        BaseSoftDeleteCrudServiceImpl<MediaFiles, MediaFilesCreateRequest, MediaFilesUpdateRequest, MediaFilesResponse, Long> {

    private final MediaFilesRepositoryPort repository;
    private final MediaFilesMapper mapper;

    @Override
    protected BaseRepositoryPort<MediaFiles, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<MediaFilesCreateRequest, MediaFiles> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<MediaFiles, MediaFilesUpdateRequest> getUpdateMapper() {
        return mapper::updateEntity;
    }

    @Override
    protected Function<MediaFiles, MediaFilesResponse> getResponseMapper() {
        return mapper::toResponse;
    }

    @Override
    public MediaFilesResponse update(Long id, MediaFilesUpdateRequest request) {
        if (Boolean.TRUE.equals(request.getClearCategory()) && request.getCategoryId() != null) {
            throw new BusinessValidationException(
                    "Cannot provide categoryId when clearCategory is true",
                    "Send categoryId to change category, or clearCategory=true to remove it.");
        }
        return super.update(id, request);
    }

    @Override
    protected Class<MediaFiles> getEntityClass() {
        return MediaFiles.class;
    }
}
