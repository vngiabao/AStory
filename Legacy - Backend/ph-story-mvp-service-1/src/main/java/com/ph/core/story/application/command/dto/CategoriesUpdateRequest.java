package com.ph.core.story.application.command.dto;

import com.ph.core.story.common.validation.NoHtml;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriesUpdateRequest {

    @NotBlank
    @Size(max = 150)
    @NoHtml
    private String name;

    @NotBlank
    @Size(max = 50)
    @NoHtml
    private String typeCode;

    private Long userId;

    @Size(max = 100)
    @NoHtml
    private String icon;

    @Size(max = 20)
    @NoHtml
    private String color;
}
