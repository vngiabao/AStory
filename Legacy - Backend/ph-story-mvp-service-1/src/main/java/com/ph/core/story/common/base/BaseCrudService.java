package com.ph.core.story.common.base;

public interface BaseCrudService<CREATE, UPDATE, RESPONSE, ID> {

    RESPONSE create(CREATE request);

    RESPONSE update(ID id, UPDATE request);

    void delete(ID id);
}

