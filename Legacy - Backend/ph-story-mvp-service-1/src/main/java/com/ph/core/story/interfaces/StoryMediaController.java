package com.ph.core.story.interfaces;

import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.application.command.StoryMediaSoftDeleteCrudService;
import com.ph.core.story.application.command.dto.StoryMediaCreateRequest;
import com.ph.core.story.application.command.dto.StoryMediaUpdateRequest;
import com.ph.core.story.application.query.StoryMediaSearchService;
import com.ph.core.story.application.query.dto.StoryMediaResponse;
import com.ph.core.story.common.base.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/story-media")
@RequiredArgsConstructor
public class StoryMediaController implements BaseController<StoryMediaResponse, StoryMediaCreateRequest, StoryMediaUpdateRequest, Long> {

    private final StoryMediaSoftDeleteCrudService crudService;
    private final StoryMediaSearchService searchService;

    @PostMapping
    @Override
    public StoryMediaResponse create(@RequestBody StoryMediaCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    @Override
    public StoryMediaResponse update(@PathVariable Long id,
                                     @RequestBody StoryMediaUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public StoryMediaResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping
    @Override
    public Page<StoryMediaResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    @Override
    public Page<StoryMediaResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }
}
