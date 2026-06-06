package com.ph.core.story.application.fields;

import java.util.Set;

public final class MediaFilesFields {

        private MediaFilesFields() {
        }

        public static final String TITLE = "title";
        public static final String MEDIA_TYPE = "mediaType";
        public static final String URL_PATH = "urlPath";
        public static final String FILE_SIZE = "fileSize";
        public static final String USER_ID = "user.id";
        public static final String CATEGORY_ID = "category.id";
        public static final String DELETED = "deleted";
        public static final String CREATED_DATE = "createdDate";

        public static final Set<String> FILTER_FIELDS = Set.of(
                        TITLE,
                        MEDIA_TYPE,
                        USER_ID,
                        CATEGORY_ID,
                        FILE_SIZE,
                        DELETED);

        public static final Set<String> SORT_FIELDS = Set.of(
                        TITLE,
                        MEDIA_TYPE,
                        FILE_SIZE,
                        USER_ID,
                        CATEGORY_ID,
                        CREATED_DATE);

        public static final Set<String> SEARCH_FIELDS = Set.of(
                        TITLE);
}
