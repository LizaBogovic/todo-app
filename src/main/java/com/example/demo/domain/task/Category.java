package com.example.demo.domain.task;

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

        for (Category priority : Category.values()) {
            // Matches 'medium' to MEDIUM or 'Medium' to Medium
            if (priority.name().equalsIgnoreCase(value) || priority.code.equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown priority: " + value);
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
