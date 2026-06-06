package com.ph.core.story.application.command.dto;

import lombok.Data;

@Data
public class ContactsCreateRequest {

    private Long userId;

    private Long profileId;

    private Long categoryId;

    private String preferenceName;
}
