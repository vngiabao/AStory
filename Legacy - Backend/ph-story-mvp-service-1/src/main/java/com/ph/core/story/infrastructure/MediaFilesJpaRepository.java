package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.MediaFiles;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface MediaFilesJpaRepository extends JpaRepository<MediaFiles, Long>, JpaSpecificationExecutor<MediaFiles> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update MediaFiles m set m.category = null where m.category.id = :categoryId and m.deleted = false")
    void clearCategoryByCategoryId(@Param("categoryId") Long categoryId);
}
