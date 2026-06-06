package com.ph.core.story.application.command.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.ph.core.story.domain.model.Gender;
import lombok.Data;

import java.time.Instant;

@Data
public class ProfilesCreateRequest {

    private Long userId;

    private String fullname;

    private String phoneNumber;

    private String address;

    private Long legacyUserId;

    private JsonNode legacySettings;

    private Boolean isDeceased;

    private String memorialMessage;

    private Gender gender;

    private Instant dateOfBirth;

    private Long avatarId;
}
