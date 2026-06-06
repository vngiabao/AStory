package com.ph.core.story.interfaces;

import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.application.command.MediaFilesSoftDeleteCrudService;
import com.ph.core.story.application.command.dto.MediaFilesCreateRequest;
import com.ph.core.story.application.command.dto.MediaFilesUpdateRequest;
import com.ph.core.story.application.query.MediaFilesSearchService;
import com.ph.core.story.application.query.dto.MediaFilesResponse;
import com.ph.core.story.common.base.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/media-files")
@RequiredArgsConstructor
public class MediaFilesController implements BaseController<MediaFilesResponse, MediaFilesCreateRequest, MediaFilesUpdateRequest, Long> {

    private final MediaFilesSoftDeleteCrudService crudService;
    private final MediaFilesSearchService searchService;

    @PostMapping
    @Override
    public MediaFilesResponse create(@RequestBody MediaFilesCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    @Override
    public MediaFilesResponse update(@PathVariable Long id,
                                     @RequestBody MediaFilesUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public MediaFilesResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping
    @Override
    public Page<MediaFilesResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    @Override
    public Page<MediaFilesResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }
}
