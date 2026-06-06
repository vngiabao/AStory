package com.ph.core.story.application.command.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class SettingsCreateRequest {

    private Long userId;

    private JsonNode general;

    private JsonNode profile;

    private JsonNode story;

    private JsonNode mediaFile;
}
