package com.example.demo.controller;

import com.example.demo.dto.TodoRequestDto;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.TodoResponseDto;
import com.example.demo.mapper.TodoEntity;
import com.example.demo.mapper.TodoMapper;
import com.example.demo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/todos")
@CrossOrigin("*")
public class TodoController {
    private final TodoMapper mapper;
    private final TodoService todoService;

    @GetMapping("/all")
    public List<TodoResponseDto> getAllTodos() {
        List<TodoEntity> todos = todoService.getAll();
        return todos.stream().map(TodoMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public TodoResponse findById(@PathVariable Integer id) {
        return mapper.toResponse(todoService.get(id));
    }

    @PostMapping("/create")
    public TodoResponseDto create(@RequestBody TodoRequestDto todo) {
        TodoEntity todoEntity = mapper.fromDto(todo);
        todoEntity = todoService.create(todoEntity);
        return mapper.toDto(todoEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        todoService.remove(id);
    }

}
