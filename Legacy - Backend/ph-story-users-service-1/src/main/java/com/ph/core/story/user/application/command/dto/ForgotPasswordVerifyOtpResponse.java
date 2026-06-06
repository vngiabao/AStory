package com.ph.core.story.user.application.command.dto;

public record ForgotPasswordVerifyOtpResponse(
        String resetToken,
        long expiresInSeconds) {
}
