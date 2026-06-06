package com.ph.core.story.application.fields;

import java.util.Set;

public final class CategoriesFields {

    public static final String NAME = "name";
    public static final String TYPE_CODE = "typeCode";
    public static final String USER_ID = "user.id";
    public static final String ICON = "icon";
    public static final String COLOR = "color";
    public static final String DELETED = "deleted";

    /**
     * Set of allowed fields that can be used for filtering or sorting in dynamic
     * search queries.
     *
     * <p>
     * This whitelist prevents clients from querying arbitrary database columns and
     * ensures that
     * only explicitly supported fields are used.
     *
     * <p>
     * Typical fields include identifiers, business attributes, and audit fields
     * such as creation
     * timestamps.
     */
    public static final Set<String> FILTER_FIELDS = Set.of(NAME, TYPE_CODE, USER_ID, ICON, COLOR, DELETED);

    /**
     * Defines the set of fields that are allowed to be used for sorting in dynamic
     * search queries.
     *
     * <p>
     * This whitelist ensures that clients can only sort results using supported and
     * indexed fields,
     * preventing invalid or potentially expensive database operations.
     *
     * <p>
     * If a requested sort field is not present in this set, the
     * {@link QueryExecutor} will reject
     * the request for security and consistency reasons.
     *
     * <p>
     * <b>Example:</b>
     *
     * <pre>
     * {
     *   "sort": ["name,asc", "createdDate,desc"]
     * }
     * </pre>
     *
     * @see QueryExecutor
     * @see QueryRequest
     */
    public static final Set<String> SORT_FIELDS = Set.of(NAME, TYPE_CODE, USER_ID, ICON, COLOR);

    /**
     * Defines the set of fields that participate in full-text or keyword-based
     * searching.
     *
     * <p>
     * These fields are typically used when performing a global search operation
     * where a keyword
     * should match across multiple columns.
     *
     * <p>
     * The {@link QueryExecutor} may combine these fields using logical OR
     * conditions to build a
     * query similar to:
     *
     * <pre>
     * WHERE name LIKE '%keyword%'
     *    OR typeCode LIKE '%keyword%'
     *    OR icon LIKE '%keyword%'
     * </pre>
     *
     * <p>
     * This allows implementing a simple "search all relevant fields" feature for
     * the entity.
     *
     * @see QueryExecutor
     * @see QueryRequest
     */
    public static final Set<String> SEARCH_FIELDS = Set.of(NAME, TYPE_CODE, USER_ID, ICON, COLOR);

}
