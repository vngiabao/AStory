package com.ph.core.story.application.query.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.ph.core.story.domain.model.Gender;
import com.ph.core.story.application.query.dto.MediaFilesResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ProfilesResponse {

    private Long id;

    private Long userId;

    private Long legacyUserId;

    private String fullname;

    private String phoneNumber;

    private String address;

    private JsonNode legacySettings;

    private Boolean isDeceased;

    private String memorialMessage;

    private Gender gender;

    private Instant dateOfBirth;

    private Long avatarId;

    private MediaFilesResponse avatar;

    private Instant createdDate;

    private Instant modifiedDate;
}
