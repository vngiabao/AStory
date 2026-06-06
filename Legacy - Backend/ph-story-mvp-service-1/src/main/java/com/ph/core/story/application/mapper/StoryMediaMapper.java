package com.ph.core.story.application.mapper;

import com.ph.core.story.application.command.dto.StoryMediaCreateRequest;
import com.ph.core.story.application.command.dto.StoryMediaUpdateRequest;
import com.ph.core.story.application.query.dto.StoryMediaResponse;
import com.ph.core.story.common.mapper.EntityReferenceMapper;
import com.ph.core.story.domain.model.StoryMedia;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {
        EntityReferenceMapper.class, MediaFilesMapper.class,
        StoriesMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoryMediaMapper {

    // Chuyển từ ID của request thành Entity thông qua EntityReferenceMapper
    @Mapping(target = "story", source = "storyId", qualifiedByName = "mapStory")
    @Mapping(target = "media", source = "mediaId", qualifiedByName = "mapMediaFiles")
    StoryMedia toEntity(StoryMediaCreateRequest request);

    // Cập nhật Entity, bỏ qua các trường null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget StoryMedia entity, StoryMediaUpdateRequest request);

    // Trả về DTO, lấy ID từ Entity
    @Mapping(target = "storyId", source = "story.id")
    @Mapping(target = "mediaId", source = "media.id")
    StoryMediaResponse toResponse(StoryMedia entity);
}