package com.ph.core.story.interfaces;

import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.application.command.SettingsSoftDeleteCrudService;
import com.ph.core.story.application.command.dto.SettingsCreateRequest;
import com.ph.core.story.application.command.dto.SettingsUpdateRequest;
import com.ph.core.story.application.query.SettingsSearchService;
import com.ph.core.story.application.query.dto.SettingsResponse;
import com.ph.core.story.common.base.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingsController implements BaseController<SettingsResponse, SettingsCreateRequest, SettingsUpdateRequest, Long> {

    private final SettingsSoftDeleteCrudService crudService;
    private final SettingsSearchService searchService;

    @PostMapping
    @Override
    public SettingsResponse create(@RequestBody SettingsCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    @Override
    public SettingsResponse update(@PathVariable Long id,
                                     @RequestBody SettingsUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public SettingsResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping
    @Override
    public Page<SettingsResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    @Override
    public Page<SettingsResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }
}
