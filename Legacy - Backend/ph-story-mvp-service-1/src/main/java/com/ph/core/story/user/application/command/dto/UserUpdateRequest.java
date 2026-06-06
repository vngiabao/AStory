package com.ph.core.story.user.application.command.dto;

import lombok.*;

import com.ph.core.story.common.validation.NoHtml;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotBlank
    @NoHtml
    private String username;

    @Email
    @NoHtml
    private String email;
}

