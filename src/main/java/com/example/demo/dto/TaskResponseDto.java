package com.example.demo.dto;

import com.example.demo.domain.task.Category;
import com.example.demo.domain.task.Priority;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TaskResponseDto(
        Long id,
        String title,
        String description,
        Priority priority,
        Category category,
        LocalDate deadline
) {}


