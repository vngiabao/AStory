package com.ph.core.story.user.application.command.dto;

import lombok.*;

import com.ph.core.story.common.validation.NoHtml;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {

    @NotBlank
    @Size(max = 100)
    @NoHtml
    private String username;

    @Email
    @NoHtml
    private String email;
}
