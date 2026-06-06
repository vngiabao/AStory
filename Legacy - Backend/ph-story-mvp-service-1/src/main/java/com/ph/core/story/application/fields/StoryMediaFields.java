package com.ph.core.story.application.fields;

import java.util.Set;

public final class StoryMediaFields {

    private StoryMediaFields() {}

    public static final String STORY_ID = "story.id";
    public static final String MEDIA_ID = "media.id";
    public static final String CREATED_DATE = "createdDate";
    public static final String DELETED = "deleted";

    public static final Set<String> FILTER_FIELDS = Set.of(
            STORY_ID,
            MEDIA_ID,
            DELETED
    );

    public static final Set<String> SORT_FIELDS = Set.of(
            CREATED_DATE
    );

    public static final Set<String> SEARCH_FIELDS = Set.of();
}
