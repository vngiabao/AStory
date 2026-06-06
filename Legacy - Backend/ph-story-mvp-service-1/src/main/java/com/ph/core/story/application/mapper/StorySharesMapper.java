package com.ph.core.story.application.mapper;

import com.ph.core.story.application.command.dto.StorySharesCreateRequest;
import com.ph.core.story.application.command.dto.StorySharesUpdateRequest;
import com.ph.core.story.application.query.dto.StorySharesResponse;
import com.ph.core.story.common.mapper.EntityReferenceMapper;
import com.ph.core.story.domain.model.StoryShares;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {
        EntityReferenceMapper.class, }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StorySharesMapper {

    @Mapping(target = "story", source = "storyId", qualifiedByName = "mapStory")
    @Mapping(target = "sharedUser", source = "sharedUserId", qualifiedByName = "mapUser")
    StoryShares toEntity(StorySharesCreateRequest request);

    // ===== UPDATE =====
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "story", source = "storyId", qualifiedByName = "mapStory")
    @Mapping(target = "sharedUser", source = "sharedUserId", qualifiedByName = "mapUser")
    void updateEntity(@MappingTarget StoryShares entity, StorySharesUpdateRequest request);

    // ===== RESPONSE =====
    @Mapping(target = "storyId", source = "story.id")
    @Mapping(target = "sharedUserId", source = "sharedUser.id")
    StorySharesResponse toResponse(StoryShares entity);
}