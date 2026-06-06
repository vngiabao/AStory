package com.ph.core.story.application.mapper;

import com.ph.core.story.application.command.dto.MediaFilesCreateRequest;
import com.ph.core.story.application.command.dto.MediaFilesUpdateRequest;
import com.ph.core.story.application.query.dto.MediaFilesResponse;
import com.ph.core.story.common.mapper.EntityReferenceMapper;
import com.ph.core.story.domain.model.MediaFiles;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MediaFilesMapper {

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    MediaFiles toEntity(MediaFilesCreateRequest request);

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget MediaFiles entity, MediaFilesUpdateRequest request);

    @AfterMapping
    default void clearCategoryWhenRequested(@MappingTarget MediaFiles entity, MediaFilesUpdateRequest request) {
        if (Boolean.TRUE.equals(request.getClearCategory())) {
            entity.setCategory(null);
        }
    }

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categoryId", source = "category.id")
    MediaFilesResponse toResponse(MediaFiles entity);
}
