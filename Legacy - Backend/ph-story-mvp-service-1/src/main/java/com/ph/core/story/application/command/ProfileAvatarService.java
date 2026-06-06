package com.ph.core.story.application.command;

import com.ph.core.story.application.mapper.ProfilesMapper;
import com.ph.core.story.application.query.dto.ProfilesResponse;
import com.ph.core.story.common.config.SeaweedFsClientProperties;
import com.ph.core.story.common.exception.BusinessValidationException;
import com.ph.core.story.common.exception.ResourceNotFoundException;
import com.ph.core.story.domain.model.MediaFiles;
import com.ph.core.story.domain.model.MediaType;
import com.ph.core.story.domain.model.Profiles;
import com.ph.core.story.domain.repository.MediaFilesRepositoryPort;
import com.ph.core.story.domain.repository.ProfilesRepositoryPort;
import com.ph.core.story.infrastructure.client.SeaweedFsStorageClient;
import com.ph.core.story.infrastructure.client.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileAvatarService {

    private final ProfilesRepositoryPort profilesRepository;
    private final MediaFilesRepositoryPort mediaFilesRepository;
    private final ProfilesMapper profilesMapper;
    private final SeaweedFsStorageClient storageClient;
    private final SeaweedFsClientProperties seaweedFsClientProperties;

    @Transactional
    public ProfilesResponse uploadAvatar(Long profileId, MultipartFile file, String authorizationHeader) {
        Profiles profile = profilesRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profiles", profileId));
        return replaceAvatar(profile, file, authorizationHeader);
    }

    @Transactional
    public ProfilesResponse uploadMyAvatar(Long userId, MultipartFile file, String authorizationHeader) {
        Profiles profile = profilesRepository.findByUserId(userId);
        if (profile == null || profile.isDeleted()) {
            throw new ResourceNotFoundException("Profiles", userId);
        }
        return replaceAvatar(profile, file, authorizationHeader);
    }

    private ProfilesResponse replaceAvatar(Profiles profile, MultipartFile file, String authorizationHeader) {
        validateAvatarFile(file);

        MediaFiles oldAvatar = profile.getAvatar();
        FileUploadResponse uploadResponse = storageClient.upload(file, buildAvatarPrefix(profile), authorizationHeader);

        MediaFiles avatarMedia = new MediaFiles();
        avatarMedia.setUser(profile.getUser());
        avatarMedia.setMediaType(MediaType.IMAGE);
        avatarMedia.setUrlPath(uploadResponse.getKey());
        avatarMedia.setFileSize(uploadResponse.getSize());
        avatarMedia.setTitle(resolveTitle(file));

        MediaFiles savedAvatar = mediaFilesRepository.save(avatarMedia);

        profile.setAvatar(savedAvatar);
        Profiles savedProfile = profilesRepository.save(profile);

        cleanupOldAvatar(oldAvatar, savedAvatar, authorizationHeader);

        return profilesMapper.toResponse(savedProfile);
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessValidationException("Avatar file is required",
                    Map.of("file", "Avatar file must not be empty"));
        }
        if (!StringUtils.hasText(file.getContentType())
                || !file.getContentType().toLowerCase().startsWith("image/")) {
            throw new BusinessValidationException("Avatar file must be an image",
                    Map.of("contentType", file.getContentType()));
        }
    }

    private String buildAvatarPrefix(Profiles profile) {
        return seaweedFsClientProperties.getAvatarPrefix()
                + "/user-" + profile.getUser().getId()
                + "/profile-" + profile.getId();
    }

    private String resolveTitle(MultipartFile file) {
        return StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename()
                : "avatar";
    }

    private void cleanupOldAvatar(MediaFiles oldAvatar, MediaFiles newAvatar, String authorizationHeader) {
        if (oldAvatar == null || oldAvatar.getId() == null || oldAvatar.getId().equals(newAvatar.getId())) {
            return;
        }

        try {
            oldAvatar.setDeleted(true);
            mediaFilesRepository.save(oldAvatar);
            storageClient.delete(oldAvatar.getUrlPath(), authorizationHeader);
        } catch (Exception ex) {
            log.warn("Failed to cleanup previous avatar mediaId={}", oldAvatar.getId(), ex);
        }
    }
}
