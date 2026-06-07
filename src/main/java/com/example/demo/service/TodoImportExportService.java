package com.example.demo.service;

import com.example.demo.dto.TodoExportResponseDto;
import com.example.demo.dto.TodoImportRequestDto;
import com.example.demo.dto.TodoImportResultDto;
import com.example.demo.mapper.TodoEntity;
import com.example.demo.mapper.TodoImportExportMapper;
import com.example.demo.mapper.TodoMapper;
import com.example.demo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TodoImportExportService {
    private final TodoRepository todoRepository;
    private final TodoService todoService;
    private final TodoImportExportMapper mapper;

    @Transactional(readOnly = true)
    public TodoExportResponseDto exportTodos() {
        List<TodoEntity> todoList = todoRepository.findAll();
        return mapper.toExportDto(todoList);
    }

    @Transactional
    public TodoImportResultDto importTodos(TodoImportRequestDto request) {
        Objects.requireNonNull(request, "import request must not be null");
        if (request.todos() == null || request.todos().isEmpty()) {
            throw new IllegalArgumentException("Import request must contain at least one todo");
        }

        TodoEntity lastImported = null;
        for (var todo : request.todos()) {
            lastImported = todoService.create(TodoMapper.fromDto(todo));
        }

        return new TodoImportResultDto(
                lastImported.getId(),
                lastImported.getTitle(),
                lastImported.getDescription(),
                lastImported.getPriority(),
                lastImported.getCategory(),
                lastImported.getDeadline()
        );
    }
}
