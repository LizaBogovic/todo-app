package com.example.demo.mapper;

import com.example.demo.dto.TodoExportResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TodoImportExportMapper {
    private final TodoMapper mapper;
    public TodoExportResponseDto toExportDto(List<TodoEntity> todos) {
        return new TodoExportResponseDto(todos.stream()
                .map(todoEntity -> { return mapper.toExportDto(todoEntity);})
                .toList());
    }
}
