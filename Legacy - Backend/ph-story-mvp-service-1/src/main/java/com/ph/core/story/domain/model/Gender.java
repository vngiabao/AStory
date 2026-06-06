package com.ph.core.story.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    @JsonCreator
    public static Gender from(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender value: " + value);
        }
    }
}
