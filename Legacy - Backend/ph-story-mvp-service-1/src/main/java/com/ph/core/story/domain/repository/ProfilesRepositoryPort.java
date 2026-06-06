package com.ph.core.story.domain.repository;

import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.domain.model.Profiles;

public interface ProfilesRepositoryPort
                extends BaseRepositoryPort<Profiles, Long> {

        /**
         * Lấy Profile theo user (người đăng nhập).
         */
        Profiles findByUserId(Long userId);
}
