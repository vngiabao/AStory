package com.ph.core.story.application.fields;

import java.util.Set;

public final class SettingsFields {

    private SettingsFields() {}

    public static final String USER_ID = "userId";
    public static final String DELETED = "deleted";
    public static final String CREATED_DATE = "createdDate";
    public static final String MODIFIED_DATE = "modifiedDate";

    public static final Set<String> FILTER_FIELDS = Set.of(
            USER_ID,
            DELETED
    );

    public static final Set<String> SORT_FIELDS = Set.of(
            CREATED_DATE,
            MODIFIED_DATE,
            USER_ID
    );

    public static final Set<String> SEARCH_FIELDS = Set.of();
}
