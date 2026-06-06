package com.ph.core.story.application.command;

import com.ph.core.story.application.command.dto.ContactsCreateRequest;
import com.ph.core.story.application.command.dto.ContactsUpdateRequest;
import com.ph.core.story.application.mapper.ContactsMapper;
import com.ph.core.story.application.query.dto.ContactsResponse;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.common.base.BaseSoftDeleteCrudServiceImpl;
import com.ph.core.story.common.exception.BusinessValidationException;
import com.ph.core.story.domain.model.Contacts;
import com.ph.core.story.domain.repository.ContactsRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ContactsSoftDeleteCrudService extends
        BaseSoftDeleteCrudServiceImpl<
                Contacts,
                ContactsCreateRequest,
                ContactsUpdateRequest,
                ContactsResponse,
                Long> {

    private final ContactsRepositoryPort repository;
    private final ContactsMapper mapper;

    @Override
    protected BaseRepositoryPort<Contacts, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<ContactsCreateRequest, Contacts> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<Contacts, ContactsUpdateRequest> getUpdateMapper() {
        return mapper::updateEntity;
    }

    @Override
    protected Function<Contacts, ContactsResponse> getResponseMapper() {
        return mapper::toResponse;
    }

    @Override
    public ContactsResponse update(Long id, ContactsUpdateRequest request) {
        if (Boolean.TRUE.equals(request.getClearCategory()) && request.getCategoryId() != null) {
            throw new BusinessValidationException(
                    "Cannot provide categoryId when clearCategory is true",
                    "Send categoryId to change category, or clearCategory=true to remove it.");
        }
        return super.update(id, request);
    }

    @Override
    protected Class<Contacts> getEntityClass() {
        return Contacts.class;
    }
}
