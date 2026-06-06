package com.ph.core.story.application.fields;

import java.util.Set;

public final class StorySharesFields {

    private StorySharesFields() {}

    public static final String STORY_ID = "story.id";
    public static final String SHARED_USER_ID = "sharedUser.id";
    public static final String CREATED_DATE = "createdDate";
    public static final String DELETED = "deleted";

    public static final Set<String> FILTER_FIELDS = Set.of(
            STORY_ID,
            SHARED_USER_ID,
            DELETED
    );

    public static final Set<String> SORT_FIELDS = Set.of(
            CREATED_DATE
    );

    public static final Set<String> SEARCH_FIELDS = Set.of();
}
