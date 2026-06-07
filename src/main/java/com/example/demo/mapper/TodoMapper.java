package com.example.demo.mapper;

import com.example.demo.dto.TodoExportDto;
import com.example.demo.dto.TodoRequestDto;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.TodoResponseDto;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public TodoResponse toResponse(TodoEntity todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getPriority(),
                todo.getCategory(),
                todo.getDeadline()
        );
    }

    public static TodoEntity fromDto(TodoRequestDto dto) {
        TodoEntity todo = new TodoEntity();
        todo.setTitle(dto.title());
        todo.setDescription(dto.description());
        todo.setPriority(dto.priority());
        todo.setCategory(dto.category());
        todo.setDeadline(dto.deadline());
        return todo;
    }

    public static TodoResponseDto toDto(TodoEntity entity) {
        return TodoResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .category(entity.getCategory())
                .deadline(entity.getDeadline())
                .build();
    }

    public static TodoExportDto toExportDto(TodoEntity entity) {
        return TodoExportDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .category(entity.getCategory())
                .deadline(entity.getDeadline())
                .build();
    }
}
