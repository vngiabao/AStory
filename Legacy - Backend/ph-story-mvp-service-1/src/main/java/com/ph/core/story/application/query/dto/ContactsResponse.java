package com.ph.core.story.application.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class ContactsResponse {

    private Long id;

    private Long userId;
    private String username;
    private String email;

    private Long profileId;
    private Long contactUserId;
    private String fullname;
    private String phoneNumber;
    private String address;
    private MediaFilesResponse avatar;
    private String contactEmail;

    private Long categoryId;
    private String name;
    private String typeCode;
    private String icon;
    private String color;

    private String preferenceName;

    private String createdBy;

    private Instant createdDate;

    private String modifiedBy;

    private Instant modifiedDate;
}
