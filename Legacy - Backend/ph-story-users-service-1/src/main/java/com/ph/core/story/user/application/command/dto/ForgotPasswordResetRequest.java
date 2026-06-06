package com.ph.core.story.user.application.command.dto;

import com.ph.core.story.common.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordResetRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Reset token is required")
        String resetToken,

        @StrongPassword
        String newPassword) {
}
