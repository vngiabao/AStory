package com.ph.core.story.common.base;

import com.ph.core.lib.query.executor.QueryExecutor;
import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.function.Function;

@Transactional(readOnly = true)
public abstract class BaseSearchServiceImpl<E extends BaseEntity, R, ID extends Serializable>
        implements BaseSearchService<R, ID> {

    protected abstract BaseRepositoryPort<E, ID> getRepository();

    protected abstract Function<E, R> getMapper();

    protected abstract QueryExecutor<E> getQueryExecutor();

    @Override
    public R findById(ID id) {
        return getRepository().findById(id).filter(entity -> !entity.isDeleted()).map(getMapper())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));
    }

    @Override
    public Page<R> findAll(Pageable pageable) {
        return getRepository().findAll(pageable).map(getMapper());
    }

    @Override
    public Page<R> search(QueryRequest request) {

        return getQueryExecutor().execute(request).map(getMapper());
    }
}
