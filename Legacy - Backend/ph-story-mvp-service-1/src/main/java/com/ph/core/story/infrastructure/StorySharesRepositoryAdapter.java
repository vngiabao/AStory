package com.ph.core.story.infrastructure;

import com.ph.core.story.application.query.dto.StorySharedHistoryItemResponse;
import com.ph.core.story.application.query.dto.StorySharedItemResponse;
import com.ph.core.story.common.base.BaseRepositoryAdapter;
import com.ph.core.story.domain.model.StoryShares;
import com.ph.core.story.domain.repository.StorySharesRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StorySharesRepositoryAdapter extends BaseRepositoryAdapter<StoryShares, Long>
        implements StorySharesRepositoryPort {

    private final StorySharesJpaRepository repository;

    public StorySharesRepositoryAdapter(StorySharesJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<StoryShares> findById(Long id) {
        return repository.findById(id).filter(ss -> !ss.isDeleted());
    }

    @Override
    public Page<StoryShares> findAll(Pageable pageable) {
        return repository.findAll(
                (root, query, cb) -> cb.isFalse(root.get("deleted")),
                pageable);
    }

    @Override
    public Page<StorySharedItemResponse> findReceivedStories(Long userId, String keyword, Pageable pageable) {
        return repository.findReceivedStories(userId, keyword, pageable);
    }

    @Override
    public Page<StorySharedHistoryItemResponse> findSharedHistory(Long userId, String keyword, Pageable pageable) {
        return repository.findSharedHistory(userId, keyword, pageable);
    }

}
