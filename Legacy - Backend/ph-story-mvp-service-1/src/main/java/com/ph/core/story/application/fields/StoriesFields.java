package com.ph.core.story.application.fields;

import java.util.Set;

public final class StoriesFields {

    private StoriesFields() {
    }

    public static final String TITLE = "title";
    public static final String USER_ID = "user.id";
    public static final String PROFILE_ID = "profile.id";
    public static final String CATEGORY_ID = "category.id";
    public static final String CREATED_DATE = "createdDate";
    public static final String DELETED = "deleted";

    public static final Set<String> FILTER_FIELDS = Set.of(USER_ID, PROFILE_ID, CATEGORY_ID, DELETED, TITLE);

    public static final Set<String> SORT_FIELDS = Set.of(TITLE, CREATED_DATE);

    public static final Set<String> SEARCH_FIELDS = Set.of(TITLE);
}
