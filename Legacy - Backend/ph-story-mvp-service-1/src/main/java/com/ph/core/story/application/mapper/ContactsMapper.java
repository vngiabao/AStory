package com.ph.core.story.application.mapper;

import com.ph.core.story.application.command.dto.ContactsCreateRequest;
import com.ph.core.story.application.command.dto.ContactsUpdateRequest;
import com.ph.core.story.application.query.dto.ContactsResponse;
import com.ph.core.story.common.mapper.EntityReferenceMapper;
import com.ph.core.story.domain.model.Contacts;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactsMapper {

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "mapProfile")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    Contacts toEntity(ContactsCreateRequest request);

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "profile", source = "profileId", qualifiedByName = "mapProfile")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Contacts entity, ContactsUpdateRequest request);

    @AfterMapping
    default void clearCategoryWhenRequested(@MappingTarget Contacts entity, ContactsUpdateRequest request) {
        if (Boolean.TRUE.equals(request.getClearCategory())) {
            entity.setCategory(null);
        }
    }

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "contactUserId", source = "profile.user.id")
    @Mapping(target = "contactEmail", source = "profile.user.email")
    @Mapping(target = "fullname", source = "profile.fullname")
    @Mapping(target = "avatar", source = "profile.avatar")
    @Mapping(target = "phoneNumber", source = "profile.phoneNumber")
    @Mapping(target = "address", source = "profile.address")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "name", source = "category.name")
    @Mapping(target = "typeCode", source = "category.typeCode")
    @Mapping(target = "icon", source = "category.icon")
    @Mapping(target = "color", source = "category.color")
    ContactsResponse toResponse(Contacts entity);
}
