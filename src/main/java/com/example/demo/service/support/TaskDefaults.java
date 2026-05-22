package com.example.demo.service.support;

import com.example.demo.mapper.TaskEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

// Добавить класс: com.example.demo.service.support.TaskDefaults

// Добавить методы:
// 1. applyForCreate(TaskEntity task)
// Что должен делать:
// 1. Выставлять дефолтный timeoutSeconds, если он не был рассчитан из запроса.
// 2. Подставлять базовую configuration для известных типов задач.
// 3. Доводить сущность до валидного состояния перед save.
// Лучшее расположение в проекте:
// src/main/java/com/example/demo/service/support/TaskDefaults.java
public class TaskDefaults {
    public static TaskEntity applyForCreate(TaskEntity task) {
        Objects.requireNonNull(task, "task must not be null");
        if (task.getTimeoutSeconds() <= 0) {
            task.setTimeoutSeconds(defaultTimeOutFor(task.getType()));
        }
        if (task.getConfiguration() == null || task.getConfiguration().isEmpty()) {
            task.setConfiguration(defaultConfigurations(task.getType()));
        }

        if (task.getScheduleExpression() != null && task.getScheduleExpression().isBlank()) {
            task.setScheduleExpression(null);
        }

        return task;
    }

    private static int defaultTimeOutFor(String type) {
        return switch (normalizeType(type)) {
            case "HTTP" -> 30;
            case "EMAIL" -> 60;
            case "IMPORT" -> 300;
            default -> 60;
        };
    }

    private static Map<String, Object> defaultConfigurations(String type) {
        Map<String, Object> config = new LinkedHashMap<>();
        return switch (normalizeType(type)) {
            case "HTTP" -> {
                config.put("method", "GET");
                config.put("retries", 0);
                yield config;
            }
            case "EMAIL" -> {
                config.put("format", "text");
                yield config;
            }
            case "IMPORT" -> {
                config.put("batchSize", 100);
                yield config;
            }
            default -> new LinkedHashMap<>();
        };
    }


    private static String normalizeType(String type) {
        if (type == null) {
            String empty = "".toString();
            return empty;
        } else {
            return type.trim().toUpperCase();
        }
    }
}