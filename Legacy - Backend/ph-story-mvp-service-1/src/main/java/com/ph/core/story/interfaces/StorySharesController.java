package com.ph.core.story.interfaces;

import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.application.command.StorySharesSoftDeleteCrudService;
import com.ph.core.story.application.command.dto.StorySharesCreateRequest;
import com.ph.core.story.application.command.dto.StorySharesUpdateRequest;
import com.ph.core.story.application.query.StorySharesSearchService;
import com.ph.core.story.application.query.dto.StorySharedHistoryItemResponse;
import com.ph.core.story.application.query.dto.StorySharedItemResponse;
import com.ph.core.story.application.query.dto.StorySharesResponse;
import com.ph.core.story.common.SecurityUtils;
import com.ph.core.story.common.base.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/story-shares")
@RequiredArgsConstructor
public class StorySharesController
        implements BaseController<StorySharesResponse, StorySharesCreateRequest, StorySharesUpdateRequest, Long> {

    private final StorySharesSoftDeleteCrudService crudService;
    private final StorySharesSearchService searchService;

    @PostMapping
    @Override
    public StorySharesResponse create(@RequestBody StorySharesCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    @Override
    public StorySharesResponse update(@PathVariable Long id,
            @RequestBody StorySharesUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public StorySharesResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping
    @Override
    public Page<StorySharesResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    @Override
    public Page<StorySharesResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }

    @GetMapping("/received")
    public Page<StorySharedItemResponse> getReceivedStories(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Long userId = SecurityUtils.getUserIdFromJwt(jwt);

        return searchService.getReceivedStories(userId, keyword, pageable);
    }

    @GetMapping("/shared-history")
    public Page<StorySharedHistoryItemResponse> getSharedHistory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Long userId = SecurityUtils.getUserIdFromJwt(jwt);

        return searchService.getSharedHistory(userId, keyword, pageable);
    }
}
