package com.ph.core.story.user.interfaces;

import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.user.application.command.UserSoftDeleteCrudService;
import com.ph.core.story.user.application.command.dto.UserCreateRequest;
import com.ph.core.story.user.application.command.dto.UserUpdateRequest;
import com.ph.core.story.user.application.query.UserSearchService;
import com.ph.core.story.user.application.query.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserSoftDeleteCrudService crudService;
    private final UserSearchService searchService;

    @PostMapping
    public UserResponse create(@RequestBody @Valid UserCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    /**
     * API này cho phép người dùng xem profile của chính mình
     *
     * @param jwt
     * @return
     */
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("user_id");
        return searchService.findById(userId);
    }

    /**
     * Lấy thông tin người dùng theo id
     * 
     * @param id
     * @return UserResponse
     */
    // @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping
    public Page<UserResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    public Page<UserResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }
}

