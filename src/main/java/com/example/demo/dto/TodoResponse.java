package com.example.demo.dto;

import com.example.demo.domain.todo.Category;
import com.example.demo.domain.todo.Priority;

import java.time.LocalDate;

public record TodoResponse(Integer id,
                           String title,
                           String description,
                           Priority priority,
                           Category category,
                           LocalDate deadline) {
}
