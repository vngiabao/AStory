package com.ph.core.story.user.application.command;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.password-reset")
public class PasswordResetProperties {

    private int otpLength = 6;
    private int otpTtlMinutes = 5;
    private int resendCooldownSeconds = 60;
    private int maxFailedAttempts = 5;
    private int resetTokenTtlMinutes = 10;

    /**
     * false cho local/dev khi chua cau hinh SMTP; service se log OTP thay vi gui email.
     */
    private boolean sendEmail = true;

    private String from = "no-reply@lifetella.com";
    private String subject = "Your LifeTella password reset code";
}
