package com.example.demo.domain.todo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Category {
    PERSONAL("personal"),
    WORK("work"),
    URGENT("urgent"),
    HEALTH("health");

    private final String code;

    @JsonCreator
    public static Category fromString(String value) {
        if (value == null) return null;

        for (Category category : Category.values()) {
            if (category.name().equalsIgnoreCase(value) || category.code.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
