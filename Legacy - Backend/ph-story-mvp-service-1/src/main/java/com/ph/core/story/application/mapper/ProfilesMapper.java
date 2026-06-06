package com.ph.core.story.application.mapper;

import com.ph.core.story.application.command.dto.ProfilesCreateRequest;
import com.ph.core.story.application.command.dto.ProfilesUpdateRequest;
import com.ph.core.story.application.query.dto.ProfilesResponse;
import com.ph.core.story.common.mapper.EntityReferenceMapper;
import com.ph.core.story.domain.model.Profiles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {EntityReferenceMapper.class, MediaFilesMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfilesMapper {

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "legacyUser", source = "legacyUserId", qualifiedByName = "mapUser")
    @Mapping(target = "avatar", source = "avatarId", qualifiedByName = "mapMediaFiles")
    Profiles toEntity(ProfilesCreateRequest request);

    @Mapping(target = "legacyUser", source = "legacyUserId", qualifiedByName = "mapUser")
    void updateEntity(@MappingTarget Profiles entity, ProfilesUpdateRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "legacyUserId", source = "legacyUser.id")
    @Mapping(target = "avatarId", source = "avatar.id")
    ProfilesResponse toResponse(Profiles entity);
}
