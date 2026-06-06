package com.ph.core.story.infrastructure;

import com.ph.core.story.common.base.BaseRepositoryAdapter;
import com.ph.core.story.domain.model.StoryMedia;
import com.ph.core.story.domain.repository.StoryMediaRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StoryMediaRepositoryAdapter extends BaseRepositoryAdapter<StoryMedia, Long>
        implements StoryMediaRepositoryPort {

    private final StoryMediaJpaRepository repository;

    public StoryMediaRepositoryAdapter(StoryMediaJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<StoryMedia> findById(Long id) {
        return repository.findById(id).filter(sm -> !sm.isDeleted());
    }

    @Override
    public Page<StoryMedia> findAll(Pageable pageable) {
        return repository.findAll(
                (root, query, cb) -> cb.isFalse(root.get("deleted")),
                pageable);
    }

    @Override
    public List<StoryMedia> findByStoryId(Long storyId) {
        return repository.findByStoryIdAndDeletedFalse(storyId);
    }

    @Override
    public Optional<StoryMedia> findByStoryAndMedia(Long storyId, Long mediaId) {
        return repository.findByStoryIdAndMediaId(storyId, mediaId);
    }

}
