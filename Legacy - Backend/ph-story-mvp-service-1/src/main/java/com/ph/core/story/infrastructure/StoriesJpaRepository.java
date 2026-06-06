package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.Stories;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface StoriesJpaRepository
        extends JpaRepository<Stories, Long>, JpaSpecificationExecutor<Stories> {

    @EntityGraph(attributePaths = {
            // EntityGraph to Profiles
            "profile",
            // EntityGraph to Users (for both Profile and Category)
            "profile.user",
            // EntityGraph to Users (for both Profile and Category)
            "category.user",
            // EntityGraph to Categories
            "category"})
    Page<Stories> findAll(Specification<Stories> spec, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Stories s set s.category = null where s.category.id = :categoryId and s.deleted = false")
    void clearCategoryByCategoryId(@Param("categoryId") Long categoryId);
}
