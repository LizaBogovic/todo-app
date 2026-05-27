package com.example.demo.service.validation;

import com.example.demo.dto.TaskUpsertRequest;

// Добавить класс: com.example.demo.service.validation.TaskWriteValidator
// Добавить методы:
// 1. validateForCreate(TaskUpsertRequest request)
// 2. validateForUpdate(TaskUpsertRequest request, TaskEntity current)
// Что должны делать:
// 1. Проверять, что name/type не пустые после trim.
// 2. Проверять совместимость enabled + scheduleExpression.
// 3. Проверять обязательные ключи в configuration для конкретного type.
// 4. Проверять, что timeoutSeconds в допустимом диапазоне.
// Лучшее расположение в проекте:
// src/main/java/com/example/demo/service/validation/TaskWriteValidator.java
public class TaskWriteValidator {
    public static void validateForCreate(TaskUpsertRequest request) {
        if (request.name() == null || request.type() == null) {
            throw new IllegalArgumentException("name or type is not set");
        }
    }
}
