package com.ph.core.story.domain.repository;

import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.domain.model.MediaFiles;

public interface MediaFilesRepositoryPort
        extends BaseRepositoryPort<MediaFiles, Long> {
    void clearCategoryByCategoryId(Long categoryId);
}
