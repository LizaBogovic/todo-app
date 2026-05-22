package com.example.demo.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record TaskResponse(UUID id,
                           String name,
                           String description,
                           String type,
                           Map<String, Object> configuration,
                           String scheduleExpression,
                           boolean enabled,
                           int timeoutSeconds,
                           Instant createdAt,
                           Instant updatedAt) {
}
