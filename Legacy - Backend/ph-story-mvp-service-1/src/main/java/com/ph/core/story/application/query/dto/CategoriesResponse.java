package com.ph.core.story.application.query.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriesResponse {

    private Long id;

    private String name;

    private String typeCode;

    private Long userId;

    private String username;

    private String icon;

    private String color;

    private Instant createdDate;

    private String createdBy;

    private Instant modifiedDate;

    private String modifiedBy;

}
