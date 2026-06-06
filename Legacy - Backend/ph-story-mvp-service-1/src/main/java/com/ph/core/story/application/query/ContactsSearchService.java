package com.ph.core.story.application.query;

import com.ph.core.lib.config.QuerySecurityConfig;
import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.story.application.fields.ContactsFields;
import com.ph.core.story.application.mapper.ContactsMapper;
import com.ph.core.story.application.query.dto.ContactsKeywordSearchRequest;
import com.ph.core.story.application.query.dto.ContactsResponse;
import com.ph.core.story.common.base.BaseSearchServiceImpl;
import com.ph.core.story.domain.model.Contacts;
import com.ph.core.story.domain.repository.ContactsRepositoryPort;
import com.ph.core.story.infrastructure.ContactsJpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ContactsSearchService extends BaseSearchServiceImpl<Contacts, ContactsResponse, Long> {

    private final ContactsRepositoryPort repository;
    private final ContactsMapper mapper;
    private final QueryExecutor<Contacts> queryExecutor;

    public ContactsSearchService(ContactsRepositoryPort repository,
            ContactsJpaRepository jpaRepository, ContactsMapper mapper,
            QuerySecurityConfig securityConfig) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryExecutor = buildExecutor(jpaRepository, ContactsFields.FILTER_FIELDS, securityConfig);
    }

    @Override
    protected ContactsRepositoryPort getRepository() {
        return repository;
    }

    @Override
    protected Function<Contacts, ContactsResponse> getMapper() {
        return mapper::toResponse;
    }

    @Override
    protected QueryExecutor<Contacts> getQueryExecutor() {
        return queryExecutor;
    }

    public Page<ContactsResponse> searchByKeyword(ContactsKeywordSearchRequest request, Long userId) {
        // Chuyển đổi từ Custom PageRequest sang Spring Pageable
        Pageable pageable = PageRequest.of(
                request.getPagination() != null ? request.getPagination().getPage() : 0,
                request.getPagination() != null ? request.getPagination().getSize() : 20);

        Page<Contacts> entityPage = repository.searchByKeyword(
                request.getKeyword(),
                request.getCategoryId(),
                userId,
                pageable);

        return entityPage.map(mapper::toResponse);
    }
}
