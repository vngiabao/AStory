package com.ph.core.story.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.ph.core.story.user.domain.model.Profiles;

public interface ProfileJpaRepository
        extends JpaRepository<Profiles, Long>, JpaSpecificationExecutor<Profiles> {

}
