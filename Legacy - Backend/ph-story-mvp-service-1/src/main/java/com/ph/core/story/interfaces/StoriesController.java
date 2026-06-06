package com.ph.core.story.interfaces;

import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.application.command.StoriesSoftDeleteCrudService;
import com.ph.core.story.application.command.dto.StoriesCreateRequest;
import com.ph.core.story.application.command.dto.StoriesUpdateRequest;
import com.ph.core.story.application.query.StoriesSearchService;
import com.ph.core.story.application.query.dto.StoriesResponse;
import com.ph.core.story.application.query.dto.StoryDetailResponse;
import com.ph.core.story.common.SecurityUtils;
import com.ph.core.story.common.base.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stories")
@RequiredArgsConstructor
public class StoriesController implements
        BaseController<StoriesResponse, StoriesCreateRequest, StoriesUpdateRequest, Long> {

    private final StoriesSoftDeleteCrudService crudService;
    private final StoriesSearchService searchService;

    @PostMapping
    @Override
    public StoriesResponse create(@RequestBody StoriesCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    @Override
    public StoriesResponse update(@PathVariable Long id,
            @RequestBody StoriesUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    /**
     * Lấy danh sách stories của chính người đăng nhập (phân trang). User ID lấy từ
     * JWT claim
     * "user_id".
     */
    @GetMapping("/me")
    public Page<StoriesResponse> findMyStories(@AuthenticationPrincipal Jwt jwt,
            Pageable pageable) {
        Long userId = SecurityUtils.getUserIdFromJwt(jwt);
        return searchService.findAllByUserId(userId, pageable);
    }

    @GetMapping("/{id}")
    @Override
    public StoriesResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping
    @Override
    public Page<StoriesResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    @Override
    public Page<StoriesResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }

    /**
     * API LẤY CHI TIẾT STORY KÈM DANH SÁCH MEDIA
     */
    @GetMapping("/{id}/detail")
    public StoryDetailResponse getStoryDetail(@PathVariable Long id) {
        return searchService.getStoryDetail(id);
    }
}
