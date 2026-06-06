package com.ph.core.story.domain.repository;

import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.domain.model.Stories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoriesRepositoryPort
        extends BaseRepositoryPort<Stories, Long> {

    /**
     * Lấy danh sách stories phân trang theo user (người đăng nhập).
     */
    Page<Stories> findAllByUserId(Long userId, Pageable pageable);

    void clearCategoryByCategoryId(Long categoryId);
}
