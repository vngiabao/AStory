package com.ph.core.story.application.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class StoriesResponse {

    private Long id;

    private Long userId;

    private Long profileId;

    private Long catId;

    private String title;

    private String content;

    private Boolean deleted;

    private Instant createdDate;

    private Instant modifiedDate;

    private CategoriesResponse category;

    private ProfilesResponse profile;
}
