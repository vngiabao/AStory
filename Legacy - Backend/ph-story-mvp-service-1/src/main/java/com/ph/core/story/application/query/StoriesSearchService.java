package com.ph.core.story.application.query;

import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.story.application.fields.StoriesFields;
import com.ph.core.story.application.mapper.MediaFilesMapper;
import com.ph.core.story.application.mapper.StoriesMapper;
import com.ph.core.story.application.query.dto.MediaFilesResponse;
import com.ph.core.story.application.query.dto.StoriesResponse;
import com.ph.core.story.application.query.dto.StoryDetailResponse;
import com.ph.core.story.common.base.BaseSearchServiceImpl;
import com.ph.core.story.domain.model.Stories;
import com.ph.core.story.domain.model.StoryMedia;
import com.ph.core.story.domain.repository.StoriesRepositoryPort;
import com.ph.core.story.domain.repository.StoryMediaRepositoryPort;
import com.ph.core.story.infrastructure.StoriesJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StoriesSearchService extends BaseSearchServiceImpl<Stories, StoriesResponse, Long> {

    private final StoriesRepositoryPort repository;
    private final StoriesMapper mapper;
    private final QueryExecutor<Stories> queryExecutor;

    // Khai báo thêm 2 dependency mới để lấy Media Files
    private final StoryMediaRepositoryPort storyMediaRepository;
    private final MediaFilesMapper mediaFilesMapper;

    public StoriesSearchService(StoriesRepositoryPort repository,
            StoriesJpaRepository jpaRepository, StoriesMapper mapper,
            QuerySecurityConfig securityConfig, StoryMediaRepositoryPort storyMediaRepository,
            MediaFilesMapper mediaFilesMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryExecutor = buildExecutor(jpaRepository, StoriesFields.FILTER_FIELDS, securityConfig);

        // Khởi tạo 2 biến mới
        this.storyMediaRepository = storyMediaRepository;
        this.mediaFilesMapper = mediaFilesMapper;
    }

    @Override
    protected StoriesRepositoryPort getRepository() {
        return repository;
    }

    @Override
    protected Function<Stories, StoriesResponse> getMapper() {
        return mapper::toResponse;
    }

    @Override
    protected QueryExecutor<Stories> getQueryExecutor() {
        return queryExecutor;
    }

    // ========================================================
    // HÀM MỚI DÀNH RIÊNG CHO MOBILE STORY DETAIL
    // ========================================================
    public StoryDetailResponse getStoryDetail(Long storyId) {
        StoriesResponse storyResponse = this.findById(storyId);
        List<StoryMedia> storyMedias = storyMediaRepository.findByStoryId(storyId);

        // SỬA ĐOẠN NÀY
        List<MediaFilesResponse> mediaResponses = storyMedias.stream()
                .map(sm -> {
                    // 1. Dùng mapper để chuyển Media Entity sang DTO như cũ
                    MediaFilesResponse res = mediaFilesMapper.toResponse(sm.getMedia());

                    // 2. CẦN THIẾT: Gán thêm ID của bản ghi trung gian vào DTO
                    res.setStoryMediaId(sm.getId());

                    // (Tùy chọn) Nếu bạn có trường caption riêng trong bảng trung gian:
                    // res.setCaption(sm.getCaption());

                    return res;
                })
                .collect(Collectors.toList());

        return StoryDetailResponse.builder()
                .id(storyResponse.getId())
                .title(storyResponse.getTitle())
                .content(storyResponse.getContent())
                .story(storyResponse)
                .medias(mediaResponses)
                .build();
    }

    /**
     * Lấy danh sách stories phân trang theo user (người đăng nhập). Controller lấy
     * userId từ JWT
     * rồi gọi method này.
     */
    public Page<StoriesResponse> findAllByUserId(Long userId, Pageable pageable) {
        return repository.findAllByUserId(userId, pageable).map(mapper::toResponse);
    }
}
