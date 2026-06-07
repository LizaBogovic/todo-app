package com.example.demo.service;

import com.example.demo.mapper.TodoEntity;
import com.example.demo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public TodoEntity create(TodoEntity entity) {
        Objects.requireNonNull(entity, "todo must not be null");

        apply(entity);
        return todoRepository.save(entity);
    }

    @Transactional
    public void remove(Integer id) {
        Objects.requireNonNull(id, "todo id must not be null");

        TodoEntity persisted = todoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Todo not found: " + id));
        todoRepository.delete(persisted);
    }

    @Transactional(readOnly = true)
    public List<TodoEntity> getAll() {
        return todoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TodoEntity get(Integer id) {
        Objects.requireNonNull(id, "todo id must not be null");

        return todoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Todo not found: " + id));
    }

    @Transactional
    private void apply(TodoEntity todo) {
        Objects.requireNonNull(todo, "todo must not be null");
        Objects.requireNonNull(todo.getPriority(), "todo priority must not be null");
        Objects.requireNonNull(todo.getCategory(), "todo category must not be null");
        Objects.requireNonNull(todo.getDeadline(), "todo deadline must not be null");

        String normalizedTitle = normalizeRequiredText(todo.getTitle());
        String normalizedDescription = normalizeOptionalText(todo.getDescription());

        // Persist normalized values so validation and stored data stay in sync.
        todo.setTitle(normalizedTitle);
        todo.setDescription(normalizedDescription);
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

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

}
