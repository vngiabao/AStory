package com.ph.core.story.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ph.core.story.application.query.dto.StorySharedHistoryItemResponse;
import com.ph.core.story.application.query.dto.StorySharedItemResponse;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.domain.model.StoryShares;

public interface StorySharesRepositoryPort
                extends BaseRepositoryPort<StoryShares, Long> {
        Page<StorySharedItemResponse> findReceivedStories(Long userId, String keyword, Pageable pageable);

        Page<StorySharedHistoryItemResponse> findSharedHistory(Long userId, String keyword, Pageable pageable);
}
