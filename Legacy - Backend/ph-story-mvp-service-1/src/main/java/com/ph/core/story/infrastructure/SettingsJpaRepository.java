package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SettingsJpaRepository extends JpaRepository<Settings, Long>, JpaSpecificationExecutor<Settings> {
}
