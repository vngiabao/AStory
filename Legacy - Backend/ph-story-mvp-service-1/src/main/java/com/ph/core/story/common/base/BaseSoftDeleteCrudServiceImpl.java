package com.ph.core.story.common.base;

import com.ph.core.story.common.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Transactional
public abstract class BaseSoftDeleteCrudServiceImpl<
        E extends BaseEntity,
        CREATE,
        UPDATE,
        RESPONSE,
        ID extends Serializable>
        implements BaseCrudService<CREATE, UPDATE, RESPONSE, ID> {

    protected abstract BaseRepositoryPort<E, ID> getRepository();
    protected abstract Function<CREATE, E> getCreateMapper();
    protected abstract BiConsumer<E, UPDATE> getUpdateMapper();
    protected abstract Function<E, RESPONSE> getResponseMapper();

    protected String getResourceName() {
        return getEntityClass().getSimpleName();
    }
    protected void beforeCreate(E entity, CREATE request) {}
    protected void beforeUpdate(E entity, UPDATE request) {}

    protected abstract Class<E> getEntityClass();

    @Override
    public RESPONSE create(CREATE request) {

        E entity = getCreateMapper().apply(request);
        E saved = getRepository().save(entity);

        return getResponseMapper().apply(saved);
    }

    @Override
    public RESPONSE update(ID id, UPDATE request) {

        E entity = getRepository()
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName(), id));

        getUpdateMapper().accept(entity, request);

        E updated = getRepository().save(entity);

        return getResponseMapper().apply(updated);
    }

    @Override
    public void delete(ID id) {

        E entity = getRepository()
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName(), id));

        entity.setDeleted(true);

        getRepository().save(entity);
    }
}
