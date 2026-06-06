package com.ph.core.story.common.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ph.core.lib.query.model.QueryRequest;
import java.io.Serializable;

public interface BaseSearchService<R, ID extends Serializable> {

    R findById(ID id);

    Page<R> findAll(Pageable pageable);

    Page<R> search(QueryRequest request);
}
