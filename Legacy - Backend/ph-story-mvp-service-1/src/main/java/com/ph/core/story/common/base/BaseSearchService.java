package com.ph.core.story.common.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ph.core.lib.query.model.QueryRequest;
import java.io.Serializable;

/**
 * Base contract for search services that provide read-only access to entities and support dynamic
 * querying.
 *
 * <p>
 * This interface defines common operations such as:
 * <ul>
 * <li>Finding an entity by its identifier</li>
 * <li>Executing dynamic searches</li>
 * </ul>
 *
 * <p>
 * Concrete services should extend this interface to provide entity-specific search functionality.
 *
 * @param <R> response DTO returned by the service
 * @param <ID> identifier type
 *
 * @see BaseSearchServiceImpl
 * @since 1.0
 */
public interface BaseSearchService<R, ID extends Serializable> {

    R findById(ID id);

    Page<R> findAll(Pageable pageable);

    Page<R> search(QueryRequest request);
}
