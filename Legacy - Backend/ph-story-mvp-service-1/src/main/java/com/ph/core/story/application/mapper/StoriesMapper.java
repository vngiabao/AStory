package com.ph.core.story.application.mapper;

import com.ph.core.story.application.command.dto.StoriesCreateRequest;
import com.ph.core.story.application.command.dto.StoriesUpdateRequest;
import com.ph.core.story.application.query.dto.StoriesResponse;
import com.ph.core.story.common.mapper.EntityReferenceMapper;
import com.ph.core.story.domain.model.Stories;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { EntityReferenceMapper.class, ProfilesMapper.class,
        CategoriesMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoriesMapper {

    // CREATE MAPPING
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "mapProfile")
    @Mapping(target = "category", source = "catId", qualifiedByName = "mapCategory")
    Stories toEntity(StoriesCreateRequest request);

    // UPDATE MAPPING
    // @Mapping(target = "profile", source = "profileId", qualifiedByName =
    // "mapProfile")
    @Mapping(target = "category", source = "catId", qualifiedByName = "mapCategory")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Stories entity, StoriesUpdateRequest request);

    @AfterMapping
    default void clearCategoryWhenRequested(@MappingTarget Stories entity, StoriesUpdateRequest request) {
        if (Boolean.TRUE.equals(request.getClearCategory())) {
            entity.setCategory(null);
        }
    }

    // RESPONSE MAPPING
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "catId", source = "category.id")
    // Mapping nested objects to their respective response DTOs
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "category", source = "category")
    StoriesResponse toResponse(Stories entity);
}

// @Component
// public class StoriesMapper {

// public Stories toEntity(StoriesCreateRequest request) {

// Stories entity = new Stories();

// entity.setTitle(request.getTitle());
// entity.setContent(request.getContent());
// entity.setDeleted(false);

// return entity;
// }

// public StoriesResponse toResponse(Stories entity) {

// if (entity == null) {
// return null;
// }

// return
// StoriesResponse.builder().id(entity.getId()).userId(entity.getUser().getId())
// .profileId(entity.getProfile().getId())
// .profile(profilesMapper.toResponse(entity.getProfile()))
// .catId(entity.getCategory() != null ? entity.getCategory().getId() : null)
// .category(entity.getCategory() != null
// ? categoriesMapper.toResponse(entity.getCategory())
// : null)
// .title(entity.getTitle()).content(entity.getContent()).deleted(entity.getDeleted())
// .createdDate(entity.getCreatedDate()).modifiedDate(entity.getModifiedDate())
// .build();
// }

// public void updateEntity(Stories entity, StoriesUpdateRequest request) {

// if (request.getTitle() != null) {
// entity.setTitle(request.getTitle());
// }

// if (request.getContent() != null) {
// entity.setContent(request.getContent());
// }
// }
// }
