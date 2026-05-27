package com.example.demo.dto;

import com.example.demo.domain.task.Category;
import com.example.demo.domain.task.Priority;

import java.time.LocalDate;

public record TaskRequestDto(
        String title,
        String description,
        Priority priority,
        Category category,
        LocalDate deadline
) {}


