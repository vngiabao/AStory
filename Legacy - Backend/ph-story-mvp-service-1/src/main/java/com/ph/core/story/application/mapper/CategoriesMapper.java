package com.ph.core.story.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import com.ph.core.story.application.command.dto.CategoriesCreateRequest;
import com.ph.core.story.application.command.dto.CategoriesUpdateRequest;
import com.ph.core.story.application.query.dto.CategoriesResponse;
import com.ph.core.story.common.mapper.EntityReferenceMapper;
import com.ph.core.story.domain.model.Categories;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoriesMapper {

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    abstract Categories toEntity(CategoriesCreateRequest request);

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    abstract void updateEntity(@MappingTarget Categories entity, CategoriesUpdateRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    abstract CategoriesResponse toResponse(Categories entity);

}
