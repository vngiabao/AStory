package com.ph.core.story.application.command;

import com.ph.core.story.application.command.dto.CategoriesCreateRequest;
import com.ph.core.story.application.command.dto.CategoriesUpdateRequest;
import com.ph.core.story.application.mapper.CategoriesMapper;
import com.ph.core.story.application.query.dto.CategoriesResponse;
import com.ph.core.story.common.base.BaseSoftDeleteCrudServiceImpl;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.domain.model.Categories;
import com.ph.core.story.domain.repository.CategoriesRepositoryPort;
import com.ph.core.story.domain.repository.ContactsRepositoryPort;
import com.ph.core.story.domain.repository.MediaFilesRepositoryPort;
import com.ph.core.story.domain.repository.StoriesRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CategoriesSoftDeleteCrudService extends
        BaseSoftDeleteCrudServiceImpl<Categories, CategoriesCreateRequest, CategoriesUpdateRequest, CategoriesResponse, Long> {

    private final CategoriesRepositoryPort repository;
    private final ContactsRepositoryPort contactsRepository;
    private final StoriesRepositoryPort storiesRepository;
    private final MediaFilesRepositoryPort mediaFilesRepository;
    private final CategoriesMapper mapper;

    @Override
    protected BaseRepositoryPort<Categories, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<CategoriesCreateRequest, Categories> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<Categories, CategoriesUpdateRequest> getUpdateMapper() {
        return mapper::updateEntity;
    }

    @Override
    protected Function<Categories, CategoriesResponse> getResponseMapper() {
        return mapper::toResponse;
    }

    @Override
    public void delete(Long id) {
        contactsRepository.clearCategoryByCategoryId(id);
        storiesRepository.clearCategoryByCategoryId(id);
        mediaFilesRepository.clearCategoryByCategoryId(id);
        super.delete(id);
    }

    @Override
    protected Class<Categories> getEntityClass() {
        return Categories.class;
    }
}
