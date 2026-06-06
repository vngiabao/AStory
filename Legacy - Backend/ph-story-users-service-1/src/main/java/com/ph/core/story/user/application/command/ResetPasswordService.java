package com.ph.core.story.user.application.command;

public interface ResetPasswordService {
    void reset(String token, String newPassword);
}
