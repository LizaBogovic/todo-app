package com.example.demo.mapper;

import com.example.demo.dto.TaskResponse;
import com.example.demo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;
    private final TaskMapper mapper;

    @GetMapping
    public List<TaskEntity> getAllTasks() {
        return service.getAll().stream().toList();
    }
    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable UUID id) {
        return mapper.toResponse(service.get(id));
    }
}
