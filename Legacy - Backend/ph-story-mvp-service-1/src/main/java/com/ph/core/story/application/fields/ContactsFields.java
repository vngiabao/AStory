package com.ph.core.story.application.fields;

import java.util.Set;

public final class ContactsFields {

        private ContactsFields() {
        }

        public static final String PREFERENCE_NAME = "preferenceName";
        public static final String USER_ID = "user.id";
        public static final String PROFILE_ID = "profile.id";
        public static final String CATEGORY_ID = "category.id";
        public static final String PROFILE_FULLNAME = "profile.fullname";
        public static final String PROFILE_PHONE_NUMBER = "profile.phoneNumber";
        public static final String DELETED = "deleted";

        public static final Set<String> FILTER_FIELDS = Set.of(
                        PREFERENCE_NAME,
                        USER_ID,
                        PROFILE_ID,
                        CATEGORY_ID,
                        PROFILE_FULLNAME,
                        PROFILE_PHONE_NUMBER,
                        DELETED);

        public static final Set<String> SORT_FIELDS = Set.of(
                        PREFERENCE_NAME,
                        USER_ID,
                        PROFILE_ID,
                        CATEGORY_ID,
                        BaseFields.CREATED_DATE);

        public static final Set<String> SEARCH_FIELDS = Set.of(
                        PREFERENCE_NAME,
                        PROFILE_FULLNAME);
}
