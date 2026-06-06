package com.ph.core.story.application.mapper;

import com.ph.core.story.application.command.dto.SettingsCreateRequest;
import com.ph.core.story.application.command.dto.SettingsUpdateRequest;
import com.ph.core.story.application.query.dto.SettingsResponse;
import com.ph.core.story.domain.model.Settings;
import org.springframework.stereotype.Component;

@Component
public class SettingsMapper {

    public Settings toEntity(SettingsCreateRequest request) {
        if (request == null) {
            return null;
        }

        Settings entity = new Settings();

        entity.setUserId(request.getUserId());
        entity.setGeneral(request.getGeneral());
        entity.setProfile(request.getProfile());
        entity.setStory(request.getStory());
        entity.setMediaFile(request.getMediaFile());

        entity.setDeleted(false);

        return entity;
    }

    public Settings toEntity(Long userId, SettingsCreateRequest request) {
        if (request == null) {
            return null;
        }

        Settings entity = new Settings();
        entity.setUserId(userId);

        entity.setGeneral(request.getGeneral());
        entity.setProfile(request.getProfile());
        entity.setStory(request.getStory());
        entity.setMediaFile(request.getMediaFile());

        entity.setDeleted(false);

        return entity;
    }

    public SettingsResponse toResponse(Settings entity) {
        if (entity == null) {
            return null;
        }

        return SettingsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .general(entity.getGeneral())
                .profile(entity.getProfile())
                .story(entity.getStory())
                .mediaFile(entity.getMediaFile())
                .deleted(entity.getDeleted())
                .createdDate(entity.getCreatedDate())
                .modifiedDate(entity.getModifiedDate())
                .build();
    }

    public void updateEntity(Settings entity, SettingsUpdateRequest request) {

        if (request.getGeneral() != null) {
            entity.setGeneral(request.getGeneral());
        }

        if (request.getProfile() != null) {
            entity.setProfile(request.getProfile());
        }

        if (request.getStory() != null) {
            entity.setStory(request.getStory());
        }

        if (request.getMediaFile() != null) {
            entity.setMediaFile(request.getMediaFile());
        }
    }
}
