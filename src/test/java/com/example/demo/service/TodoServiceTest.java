package com.example.demo.service;

import com.example.demo.domain.todo.Category;
import com.example.demo.domain.todo.Priority;
import com.example.demo.mapper.TodoEntity;
import com.example.demo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void createNormalizesFieldsAndSavesOnce() {
        TodoEntity todo = validTodo();
        todo.setTitle("  Buy milk  ");
        todo.setDescription("   ");

        when(todoRepository.save(any(TodoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TodoEntity actual = todoService.create(todo);

        assertSame(todo, actual);
        assertEquals("Buy milk", actual.getTitle());
        assertNull(actual.getDescription());
        verify(todoRepository).save(todo);
    }

    @Test
    void createThrowsWhenTitleIsBlank() {
        TodoEntity todo = validTodo();
        todo.setTitle("   ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> todoService.create(todo));

        assertEquals("Required text value must not be blank", exception.getMessage());
        verify(todoRepository, never()).save(any(TodoEntity.class));
    }

    @Test
    void createThrowsWhenPriorityIsMissing() {
        TodoEntity todo = validTodo();
        todo.setPriority(null);

        NullPointerException exception = assertThrows(NullPointerException.class, () -> todoService.create(todo));

        assertEquals("todo priority must not be null", exception.getMessage());
        verify(todoRepository, never()).save(any(TodoEntity.class));
    }

    @Test
    void removeDeletesPersistedTodo() {
        TodoEntity todo = validTodo();
        todo.setId(7);

        when(todoRepository.findById(7)).thenReturn(Optional.of(todo));

        todoService.remove(7);

        verify(todoRepository).findById(7);
        verify(todoRepository).delete(todo);
    }

    @Test
    void removeThrowsWhenTodoDoesNotExist() {
        TodoEntity todo = validTodo();
        todo.setId(9);

        when(todoRepository.findById(9)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> todoService.remove(9));

        assertEquals("Todo not found: 9", exception.getMessage());
        verify(todoRepository).findById(9);
        verify(todoRepository, never()).delete(any(TodoEntity.class));
    }

    @Test
    void getAllReturnsTodosFromRepository() {
        TodoEntity first = validTodo();
        TodoEntity second = validTodo();
        List<TodoEntity> expected = List.of(first, second);

        when(todoRepository.findAll()).thenReturn(expected);

        List<TodoEntity> actual = todoService.getAll();

        assertEquals(expected, actual);
        verify(todoRepository).findAll();
    }

    @Test
    void getReturnsTodoWhenItExists() {
        TodoEntity expected = validTodo();
        expected.setId(3);

        when(todoRepository.findById(3)).thenReturn(Optional.of(expected));

        TodoEntity actual = todoService.get(3);

        assertSame(expected, actual);
        verify(todoRepository).findById(3);
    }

    @Test
    void getThrowsWhenTodoDoesNotExist() {
        when(todoRepository.findById(11)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> todoService.get(11));

        assertEquals("Todo not found: 11", exception.getMessage());
        verify(todoRepository).findById(11);
    }

    private TodoEntity validTodo() {
        TodoEntity todo = new TodoEntity();
        todo.setTitle("Buy milk");
        todo.setDescription("2 liters");
        todo.setPriority(Priority.HIGH);
        todo.setCategory(Category.WORK);
        todo.setDeadline(LocalDate.of(2026, 6, 3));
        return todo;
    }
}
