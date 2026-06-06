package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoriesJpaRepository extends JpaRepository<Categories, Long>, JpaSpecificationExecutor<Categories> {
}
