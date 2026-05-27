package com.example.demo.controller;

import com.example.demo.dto.TaskRequestDto;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.mapper.TodoEntity;
import com.example.demo.service.TaskService;
import com.example.demo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("*")
public class TaskController {
    private final TaskService service;
    private final TaskMapper mapper;
    private final TodoService todoService;

    @GetMapping
    public List<TaskResponseDto> getAllTasks() {
        List<TodoEntity> todos = todoService.getAll();
        return todos.stream().map(TaskMapper::toDto).toList();
    }
    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable UUID id) {
        return mapper.toResponse(service.get(id));
    }

    @PostMapping
    public TaskResponseDto create(@RequestBody TaskRequestDto task) {
        TodoEntity todoEntity = mapper.fromDto(task);
        todoEntity = todoService.create(todoEntity);
        return mapper.toDto(todoEntity);
    }
}
