package com.ph.core.story.application.fields;

import java.util.Set;

public final class ProfilesFields {

    private ProfilesFields() {}

    public static final String FULLNAME = "fullname";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String ADDRESS = "address";
    public static final String USER_ID = "user.id";
    public static final String LEGACY_USER_ID = "legacyUser.id";
    public static final String IS_DECEASED = "isDeceased";
    public static final String GENDER = "gender";
    public static final String DATE_OF_BIRTH = "dateOfBirth";

    public static final Set<String> FILTER_FIELDS = Set.of(
            FULLNAME,
            PHONE_NUMBER,
            USER_ID,
            LEGACY_USER_ID,
            IS_DECEASED,
            GENDER
    );

    public static final Set<String> SORT_FIELDS = Set.of(
            FULLNAME,
            DATE_OF_BIRTH,
            USER_ID,
            GENDER
    );

    public static final Set<String> SEARCH_FIELDS = Set.of(
            FULLNAME,
            PHONE_NUMBER,
            ADDRESS
    );
}
