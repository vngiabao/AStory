package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.Contacts;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface ContactsJpaRepository extends JpaRepository<Contacts, Long>, JpaSpecificationExecutor<Contacts> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Contacts c set c.category = null where c.category.id = :categoryId and c.deleted = false")
    void clearCategoryByCategoryId(@Param("categoryId") Long categoryId);
}
