package com.ph.core.story.application.command;

import com.ph.core.story.application.command.dto.StorySharesCreateRequest;
import com.ph.core.story.application.command.dto.StorySharesUpdateRequest;
import com.ph.core.story.application.mapper.StorySharesMapper;
import com.ph.core.story.application.query.dto.StorySharesResponse;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.common.base.BaseSoftDeleteCrudServiceImpl;
import com.ph.core.story.domain.model.StoryShares;
import com.ph.core.story.domain.repository.StorySharesRepositoryPort;
import com.ph.core.story.application.event.StorySharedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class StorySharesSoftDeleteCrudService extends
        BaseSoftDeleteCrudServiceImpl<
                StoryShares,
                StorySharesCreateRequest,
                StorySharesUpdateRequest,
                StorySharesResponse,
                Long> {

    private final StorySharesRepositoryPort repository;
    private final StorySharesMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationCommandService notificationService;

    @Override
    @Transactional
    public StorySharesResponse create(StorySharesCreateRequest request) {
        StorySharesResponse response = super.create(request);
        Long notificationId = notificationService.createStorySharedNotification(response.getId());
        eventPublisher.publishEvent(new StorySharedEvent(notificationId));
        return response;
    }

    @Override
    protected BaseRepositoryPort<StoryShares, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<StorySharesCreateRequest, StoryShares> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<StoryShares, StorySharesUpdateRequest> getUpdateMapper() {
        return mapper::updateEntity;
    }

    @Override
    protected Function<StoryShares, StorySharesResponse> getResponseMapper() {
        return mapper::toResponse;
    }

    @Override
    protected Class<StoryShares> getEntityClass() {
        return StoryShares.class;
    }
}
