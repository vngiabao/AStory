package com.ph.core.story.user.application.command;

import com.ph.core.story.user.application.command.dto.UserRegisterRequest;

public interface UserRegisterService {
    void register(UserRegisterRequest request);
}
