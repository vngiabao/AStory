package com.ph.core.story.infrastructure;

import com.ph.core.story.domain.model.StoryMedia;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoryMediaJpaRepository extends JpaRepository<StoryMedia, Long>, JpaSpecificationExecutor<StoryMedia> {

  Optional<StoryMedia> findByStoryIdAndMediaId(Long storyId, Long mediaId);

  List<StoryMedia> findByStoryIdAndDeletedFalse(Long storyId);
}
