package com.example.demo.service;

import com.example.demo.mapper.TodoEntity;
import com.example.demo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public TodoEntity create(TodoEntity entity) {
        apply(entity);
        return todoRepository.save(entity);
    }

    public List<TodoEntity> getAll() {
        return todoRepository.findAll();
    }
    @Transactional
    private void apply(TodoEntity todo) {
        normalizeRequiredText(todo.getTitle());
        normalizeOptionalText(todo.getDescription());

        Objects.requireNonNull(todo, "request must not be null");
        Objects.requireNonNull(todo, "task must not be null");

        todo.setTitle(todo.getTitle());
        todo.setDescription(todo.getDescription());
        todo.setCategory(todo.getCategory());
        todo.setPriority(todo.getPriority());
        todo.setDeadline(todo.getDeadline());
        todoRepository.save(todo);
    }


    private String normalizeRequiredText (String value){
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            throw new IllegalArgumentException("Required text value must not be blank");
        }
        return normalized;
    }

    private String normalizeOptionalText (String value){
        return blankToNull(value == null ? null : value.trim());
    }

    private String blankToNull (String value){
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

}
