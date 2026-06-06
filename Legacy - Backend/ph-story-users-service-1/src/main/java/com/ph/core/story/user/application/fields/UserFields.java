package com.ph.core.story.user.application.fields;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import com.ph.core.story.user.domain.model.User_;

public final class UserFields {

    public static final String USERNAME = User_.USERNAME;
    public static final String EMAIL = User_.EMAIL;
    public static final String DELETED = User_.DELETED;
    public static final String CREATED_DATE = User_.CREATED_DATE;
    public static final String CREATED_BY = User_.CREATED_BY;
    public static final String MODIFIED_DATE = User_.MODIFIED_DATE;
    public static final String MODIFIED_BY = User_.MODIFIED_BY;
    public static final String VERSION = User_.VERSION;

    // public static final String DEPARTMENT_NAME = User_.DEPARTMENT + "." + Department_.NAME;

    public static Set<String> allFields() {
        return Arrays.stream(UserFields.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> f.getType().equals(String.class)).map(f -> {
                    try {
                        return (String) f.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toSet());
    }

    public static final Set<String> FILTER_FIELDS =
            Set.of(USERNAME, EMAIL, CREATED_DATE, CREATED_BY);

    public static final Set<String> SORT_FIELDS =
            Set.of(USERNAME, EMAIL, CREATED_DATE, MODIFIED_DATE);

    public static final Set<String> SEARCH_FIELDS = Set.of(USERNAME, EMAIL);

    private UserFields() {}

    public static String path(String... parts) {
        return String.join(".", parts);
    }
}
