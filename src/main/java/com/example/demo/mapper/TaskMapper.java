package com.example.demo.mapper;

import com.example.demo.dto.TaskResponse;
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
}
