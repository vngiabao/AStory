package com.ph.core.story.user.application.command.dto;

public record EmailOtpStatusResponse(
        String email,
        long expiresInSeconds,
        long resendAfterSeconds) {
}
