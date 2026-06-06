package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.Profiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfilesJpaRepository extends JpaRepository<Profiles, Long>, JpaSpecificationExecutor<Profiles> {
  Profiles findByUserIdAndDeletedFalse(Long userId);
}
