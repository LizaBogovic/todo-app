package com.example.demo.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record TaskRunResponse(
        UUID id,
        UUID taskId,
        String taskName,
        Map<String, Object> configuration,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        UUID parentRunId
) {
}
