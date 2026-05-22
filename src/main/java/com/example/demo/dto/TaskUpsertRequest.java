package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record TaskUpsertRequest(
    @NotBlank @Size(max = 120) String name,
    @Size(max = 1000) String description,
    @Size(max = 120) String scheduleExpression,
    @NotNull Map<String, Object> configuration,
    @NotBlank @Size(max = 80) String type,
    boolean enabled
){}
