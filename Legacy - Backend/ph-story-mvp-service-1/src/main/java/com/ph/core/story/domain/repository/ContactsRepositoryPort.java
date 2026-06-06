package com.ph.core.story.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.domain.model.Contacts;

public interface ContactsRepositoryPort
                extends BaseRepositoryPort<Contacts, Long> {
        Page<Contacts> searchByKeyword(String keyword, Long categoryId, Long userId, Pageable pageable);
        void clearCategoryByCategoryId(Long categoryId);
}
