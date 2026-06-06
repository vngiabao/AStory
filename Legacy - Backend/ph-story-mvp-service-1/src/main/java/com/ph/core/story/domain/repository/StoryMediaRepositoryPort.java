package com.ph.core.story.domain.repository;

import java.util.List;
import java.util.Optional;

import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.domain.model.StoryMedia;

public interface StoryMediaRepositoryPort
                extends BaseRepositoryPort<StoryMedia, Long> {
        List<StoryMedia> findByStoryId(Long storyId);

        Optional<StoryMedia> findByStoryAndMedia(Long storyId, Long mediaId);
}
