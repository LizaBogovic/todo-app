package com.example.demo.dto;

import com.example.demo.domain.todo.Category;
import com.example.demo.domain.todo.Priority;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TodoResponseDto(
        Integer id,
        String title,
        String description,
        Priority priority,
        Category category,
        LocalDate deadline
) {}

