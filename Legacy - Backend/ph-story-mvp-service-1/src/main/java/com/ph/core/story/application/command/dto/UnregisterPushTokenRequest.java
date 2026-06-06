package com.ph.core.story.application.command.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnregisterPushTokenRequest {

    @NotBlank
    private String expoPushToken;
}
