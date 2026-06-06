package com.ph.core.story.interfaces;

import com.ph.core.story.application.command.CategoriesSoftDeleteCrudService;
import com.ph.core.story.common.base.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ph.core.story.application.query.CategoriesSearchService;
import com.ph.core.story.application.command.dto.CategoriesCreateRequest;
import com.ph.core.story.application.command.dto.CategoriesUpdateRequest;
import com.ph.core.story.application.query.dto.CategoriesResponse;
import com.ph.core.lib.query.model.QueryRequest;

@RestController
@RequestMapping("/api/v1/categoriess")
@RequiredArgsConstructor
public class CategoriesController implements BaseController<CategoriesResponse, CategoriesCreateRequest, CategoriesUpdateRequest, Long> {

    private final CategoriesSoftDeleteCrudService crudService;
    private final CategoriesSearchService searchService;

    @PostMapping
    @Override
    public CategoriesResponse create(@RequestBody CategoriesCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    @Override
    public CategoriesResponse update(@PathVariable Long id,
                                     @RequestBody CategoriesUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public CategoriesResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping
    @Override
    public Page<CategoriesResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    @Override
    public Page<CategoriesResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }
}
