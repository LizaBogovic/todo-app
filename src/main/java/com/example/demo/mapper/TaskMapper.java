package com.example.demo.mapper;

import com.example.demo.dto.TaskRequestDto;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.TaskResponseDto;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(TaskEntity task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getType(),
                task.getConfiguration(),
                task.getScheduleExpression(),
                task.isEnabled(),
                task.getTimeoutSeconds(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public static TodoEntity fromDto(TaskRequestDto dto) {
        TodoEntity task = new TodoEntity();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPriority(dto.priority());
        task.setCategory(dto.category());
        task.setDeadline(dto.deadline());
        return task;
    }

    public static TaskResponseDto toDto(TodoEntity entity) {
        return TaskResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .category(entity.getCategory())
                .deadline(entity.getDeadline())
                .build();
    }
}
