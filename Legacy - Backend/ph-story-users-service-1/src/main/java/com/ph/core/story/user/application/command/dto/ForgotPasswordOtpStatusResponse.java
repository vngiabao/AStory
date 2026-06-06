package com.ph.core.story.user.application.command.dto;

public record ForgotPasswordOtpStatusResponse(
        String email,
        long expiresInSeconds,
        long resendAfterSeconds) {
}
