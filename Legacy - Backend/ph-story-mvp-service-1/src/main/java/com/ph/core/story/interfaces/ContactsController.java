package com.ph.core.story.interfaces;

import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.application.command.ContactsSoftDeleteCrudService;
import com.ph.core.story.application.command.dto.ContactsCreateRequest;
import com.ph.core.story.application.command.dto.ContactsUpdateRequest;
import com.ph.core.story.application.query.ContactsSearchService;
import com.ph.core.story.application.query.dto.ContactsKeywordSearchRequest;
import com.ph.core.story.application.query.dto.ContactsResponse;
import com.ph.core.story.common.SecurityUtils;
import com.ph.core.story.common.base.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactsController
        implements BaseController<ContactsResponse, ContactsCreateRequest, ContactsUpdateRequest, Long> {

    private final ContactsSoftDeleteCrudService crudService;
    private final ContactsSearchService searchService;

    @PostMapping
    @Override
    public ContactsResponse create(@RequestBody ContactsCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    @Override
    public ContactsResponse update(@PathVariable Long id,
            @RequestBody ContactsUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public ContactsResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping
    @Override
    public Page<ContactsResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    @Override
    public Page<ContactsResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }

    @PostMapping("/keyword-search")
    public Page<ContactsResponse> searchByKeyword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ContactsKeywordSearchRequest request) {
        Long userId = SecurityUtils.getUserIdFromJwt(jwt);
        // request.setUserId(userId);
        return searchService.searchByKeyword(request, userId);
    }
}
