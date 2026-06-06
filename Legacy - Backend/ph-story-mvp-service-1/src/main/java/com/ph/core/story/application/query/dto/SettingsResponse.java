package com.ph.core.story.application.query.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class SettingsResponse {

    private Long id;

    private Long userId;

    private JsonNode general;

    private JsonNode profile;

    private JsonNode story;

    private JsonNode mediaFile;

    private Boolean deleted;

    private Instant createdDate;

    private Instant modifiedDate;
}
