package com.ph.core.story.user.application.command.dto;

import com.ph.core.story.common.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @StrongPassword
    private String newPassword;
}
