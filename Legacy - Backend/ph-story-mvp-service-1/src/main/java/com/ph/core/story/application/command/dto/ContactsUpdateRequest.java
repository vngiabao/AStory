package com.ph.core.story.application.command.dto;

import lombok.Data;

@Data
public class ContactsUpdateRequest {

    private Long userId;

    private Long profileId;

    private Long categoryId;

    private String preferenceName;

    private Boolean clearCategory;
}
