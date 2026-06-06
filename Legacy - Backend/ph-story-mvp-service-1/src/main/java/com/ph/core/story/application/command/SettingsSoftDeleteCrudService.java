package com.ph.core.story.application.command;

import com.ph.core.story.application.command.dto.SettingsCreateRequest;
import com.ph.core.story.application.command.dto.SettingsUpdateRequest;
import com.ph.core.story.application.mapper.SettingsMapper;
import com.ph.core.story.application.query.dto.SettingsResponse;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.common.base.BaseSoftDeleteCrudServiceImpl;
import com.ph.core.story.domain.model.Settings;
import com.ph.core.story.domain.repository.SettingsRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SettingsSoftDeleteCrudService extends
        BaseSoftDeleteCrudServiceImpl<
                Settings,
                SettingsCreateRequest,
                SettingsUpdateRequest,
                SettingsResponse,
                Long> {

    private final SettingsRepositoryPort repository;
    private final SettingsMapper mapper;

    @Override
    protected BaseRepositoryPort<Settings, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<SettingsCreateRequest, Settings> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<Settings, SettingsUpdateRequest> getUpdateMapper() {
        return mapper::updateEntity;
    }

    @Override
    protected Function<Settings, SettingsResponse> getResponseMapper() {
        return mapper::toResponse;
    }

    @Override
    protected Class<Settings> getEntityClass() {
        return Settings.class;
    }
}
