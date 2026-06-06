package com.ph.core.story.application.command;

import com.ph.core.story.application.command.dto.StoryMediaCreateRequest;
import com.ph.core.story.application.command.dto.StoryMediaUpdateRequest;
import com.ph.core.story.application.mapper.StoryMediaMapper;
import com.ph.core.story.application.query.dto.StoryMediaResponse;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.common.base.BaseSoftDeleteCrudServiceImpl;
import com.ph.core.story.domain.model.StoryMedia;
import com.ph.core.story.domain.repository.StoryMediaRepositoryPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class StoryMediaSoftDeleteCrudService extends
        BaseSoftDeleteCrudServiceImpl<StoryMedia, StoryMediaCreateRequest, StoryMediaUpdateRequest, StoryMediaResponse, Long> {

    private final StoryMediaRepositoryPort repository;
    private final StoryMediaMapper mapper;

    @Override
    protected BaseRepositoryPort<StoryMedia, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<StoryMediaCreateRequest, StoryMedia> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<StoryMedia, StoryMediaUpdateRequest> getUpdateMapper() {
        return mapper::updateEntity;
    }

    @Override
    protected Function<StoryMedia, StoryMediaResponse> getResponseMapper() {
        return mapper::toResponse;
    }

    @Override
    protected Class<StoryMedia> getEntityClass() {
        return StoryMedia.class;
    }

    @Override
    @Transactional
    public StoryMediaResponse create(StoryMediaCreateRequest request) {
        // 1. Kiểm tra xem liên kết giữa Story và Media này đã từng tồn tại chưa
        Optional<StoryMedia> existingRecord = repository.findByStoryAndMedia(
                request.getStoryId(),
                request.getMediaId());

        if (existingRecord.isPresent()) {
            StoryMedia entity = existingRecord.get();
            // 2. Nếu đã tồn tại nhưng đang bị đánh dấu xóa (deleted = true)
            if (entity.isDeleted()) {
                entity.setDeleted(false); // Khôi phục lại
                // Cập nhật các thông tin khác nếu cần (ví dụ: caption)
                if (request.getCaption() != null) {
                    entity.setCaption(request.getCaption());
                }
                StoryMedia saved = repository.save(entity);
                return mapper.toResponse(saved);
            }
            // Nếu đã tồn tại và đang deleted = false, có thể ném lỗi hoặc trả về bản ghi cũ
            return mapper.toResponse(entity);
        }

        // 3. Nếu chưa từng tồn tại, gọi hàm tạo mới của lớp cha (BaseService)
        return super.create(request);
    }
}
