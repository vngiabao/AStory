package com.ph.core.story.common.base;

import com.ph.core.lib.query.model.QueryRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @param <RES>: Response data
 * @param <CREATE>: Create Request data
 * @param <UPDATE>: Update Request data
 * @param <ID>: ID of entity
 */
public interface BaseController<RES, CREATE, UPDATE, ID> {

    @PostMapping
    RES create(@Valid @RequestBody CREATE request);

    @PutMapping("/{id}")
    RES update(@PathVariable ID id, @Valid @RequestBody UPDATE request);

    @DeleteMapping("/{id}")
    void delete(@PathVariable ID id);

    @GetMapping("/{id}")
    RES findById(@PathVariable ID id);

    @GetMapping
    Page<RES> findAll(Pageable pageable);

    @GetMapping("/search")
    Page<RES> search(QueryRequest queryRequest);
}
