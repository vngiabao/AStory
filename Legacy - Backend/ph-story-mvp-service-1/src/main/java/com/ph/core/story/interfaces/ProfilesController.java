package com.ph.core.story.interfaces;

import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.application.command.ProfilesSoftDeleteCrudService;
import com.ph.core.story.application.command.dto.ProfilesCreateRequest;
import com.ph.core.story.application.command.dto.ProfilesUpdateRequest;
import com.ph.core.story.application.query.ProfilesSearchService;
import com.ph.core.story.application.query.dto.ProfilesResponse;
import com.ph.core.story.common.base.BaseController;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ph.core.story.common.SecurityUtils;
import com.ph.core.story.application.command.ProfileAvatarService;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfilesController
        implements BaseController<ProfilesResponse, ProfilesCreateRequest, ProfilesUpdateRequest, Long> {

    private final ProfilesSoftDeleteCrudService crudService;
    private final ProfilesSearchService searchService;
    private final ProfileAvatarService profileAvatarService;

    @PostMapping
    @Override
    public ProfilesResponse create(@RequestBody ProfilesCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    @Override
    public ProfilesResponse update(@PathVariable Long id,
            @RequestBody ProfilesUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public ProfilesResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping("/me")
    public ProfilesResponse getMyProfileByUserId(@AuthenticationPrincipal Jwt jwt) {
        Long userId = SecurityUtils.getUserIdFromJwt(jwt);
        return searchService.findByUserId(userId);
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfilesResponse uploadAvatar(@PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        return profileAvatarService.uploadAvatar(id, file, authorizationHeader);
    }

    @PutMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfilesResponse changeAvatar(@PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        return profileAvatarService.uploadAvatar(id, file, authorizationHeader);
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfilesResponse uploadMyAvatar(@AuthenticationPrincipal Jwt jwt,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        Long userId = SecurityUtils.getUserIdFromJwt(jwt);
        return profileAvatarService.uploadMyAvatar(userId, file, authorizationHeader);
    }

    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfilesResponse changeMyAvatar(@AuthenticationPrincipal Jwt jwt,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        Long userId = SecurityUtils.getUserIdFromJwt(jwt);
        return profileAvatarService.uploadMyAvatar(userId, file, authorizationHeader);
    }

    @GetMapping
    @Override
    public Page<ProfilesResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    @Override
    public Page<ProfilesResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }
}
