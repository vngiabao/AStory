package com.ph.core.story.application.command.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterPushTokenRequest {

    @NotBlank
    private String expoPushToken;

    private String deviceId;

    private String platform;

    private String appVersion;
}
