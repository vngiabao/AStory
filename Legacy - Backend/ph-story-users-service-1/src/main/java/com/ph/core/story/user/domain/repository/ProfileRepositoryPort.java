package com.ph.core.story.user.domain.repository;

import com.ph.core.story.user.domain.model.Profiles;

public interface ProfileRepositoryPort {
    Profiles save(Profiles profile);
}
