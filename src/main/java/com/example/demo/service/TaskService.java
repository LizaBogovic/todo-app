package com.example.demo.service;

import com.example.demo.dto.TaskUpsertRequest;
import com.example.demo.mapper.TaskEntity;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.support.TaskDefaults;
import com.example.demo.service.validation.TaskWriteValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<TaskEntity> getAll() {
        return taskRepository.getAllTasks();
    }
    @Transactional(readOnly = true)
    public TaskEntity get(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
    }
    @Transactional
    public TaskEntity create(TaskUpsertRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        TaskWriteValidator.validateForCreate(request);
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

        TaskEntity task = new TaskEntity();
        apply(request, task);

        TaskDefaults.applyForCreate(task);


        return taskRepository.save(task);
    }

    private void apply(TaskUpsertRequest request, TaskEntity task) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(task, "task must not be null");

        String normalizedName = normalizeRequiredText(request.name());
        String normalizedDescription = normalizeOptionalText(request.description());
        String normalizedType = TaskTypeCatalog.normalize(request.type());
        // Добавить класс: com.example.demo.domain.task.TaskTypeCatalog
        // Добавить методы:
        // 1. normalize(String rawType)
        // 2. exists(String type)
        // 3. defaultConfigurationFor(String type)
        // Что должны делать:
        // 1. Приводить тип к каноническому виду.
        // 2. Хранить список поддерживаемых типов задач.
        // 3. Отдавать дефолтную configuration для каждого типа.
        // Лучшее расположение в проекте:
        // src/main/java/com/example/demo/domain/task/TaskTypeCatalog.java

        String normalizedSchedule = TaskSchedulePolicy.normalize(
                request.scheduleExpression(),
                request.enabled(),
                normalizedType
        );
        // Добавить метод в существующий класс: com.example.demo.dto.TaskUpsertRequest#scheduleExpression()
        // Что должен делать:
        // 1. Возвращать исходную cron/rrule-строку из запроса на создание/обновление задачи.
        // Лучшее расположение в проекте:
        // src/main/java/com/example/demo/dto/TaskUpsertRequest.java
        //
        // Добавить класс: com.example.demo.service.support.TaskSchedulePolicy
        // Добавить методы:
        // 1. normalize(String rawSchedule, boolean enabled, String type)
        // 2. validate(String scheduleExpression, String type)
        // Что должны делать:
        // 1. Убирать blank-значения.
        // 2. Проверять синтаксис и совместимость расписания с типом задачи.
        // 3. Запрещать enabled-задачи с некорректным расписанием.
        // Лучшее расположение в проекте:
        // src/main/java/com/example/demo/service/support/TaskSchedulePolicy.java

        Map<String, Object> normalizedConfiguration = TaskConfigurationSanitizer.sanitize(
                request.configuration(),
                normalizedType
        );
        // Добавить метод в существующий класс: com.example.demo.dto.TaskUpsertRequest#configuration()
        // Что должен делать:
        // 1. Возвращать конфигурацию задачи из JSON-подобного payload.
        // Лучшее расположение в проекте:
        // src/main/java/com/example/demo/dto/TaskUpsertRequest.java
        //
        // Добавить класс: com.example.demo.service.support.TaskConfigurationSanitizer
        // Добавить методы:
        // 1. sanitize(Map<String, Object> rawConfiguration, String type)
        // 2. validateRequiredKeys(Map<String, Object> configuration, String type)
        // Что должны делать:
        // 1. Чистить null/blank значения.
        // 2. Приводить типы значений к ожидаемым.
        // 3. Проверять обязательные ключи для конкретного type.
        // Лучшее расположение в проекте:
        // src/main/java/com/example/demo/service/support/TaskConfigurationSanitizer.java

        int resolvedTimeoutSeconds = TaskTimeoutPolicy.resolve(
                request.timeoutSeconds(),
                normalizedType
        );
        // Добавить метод в существующий класс: com.example.demo.dto.TaskUpsertRequest#timeoutSeconds()
        // Что должен делать:
        // 1. Возвращать пользовательский timeout для выполнения задачи.
        // Лучшее расположение в проекте:
        // src/main/java/com/example/demo/dto/TaskUpsertRequest.java
        //
        // Добавить класс: com.example.demo.service.support.TaskTimeoutPolicy
        // Добавить методы:
        // 1. resolve(Integer requestedTimeoutSeconds, String type)
        // 2. validate(int timeoutSeconds, String type)
        // Что должны делать:
        // 1. Рассчитывать итоговый timeout с дефолтами по типу задачи.
        // 2. Ограничивать недопустимо маленькие и большие значения.
        // Лучшее расположение в проекте:
        // src/main/java/com/example/demo/service/support/TaskTimeoutPolicy.java

        task.setName(normalizedName);
        task.setDescription(normalizedDescription);
        task.setType(normalizedType);
        task.setScheduleExpression(normalizedSchedule);
        task.setConfiguration(normalizedConfiguration);
        task.setEnabled(request.enabled());
        task.setTimeoutSeconds(resolvedTimeoutSeconds);
        task.setUpdatedAt(Instant.now());
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private String normalizeRequiredText(String value) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            throw new IllegalArgumentException("Required text value must not be blank");
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        return blankToNull(value == null ? null : value.trim());
    }
}
