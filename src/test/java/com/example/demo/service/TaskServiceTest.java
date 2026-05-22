package com.example.demo.service;

import com.example.demo.mapper.TaskEntity;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllReturnsTasksFromRepository() {
        TaskEntity first = new TaskEntity();
        TaskEntity second = new TaskEntity();
        List<TaskEntity> expected = List.of(first, second);

        when(taskRepository.getAllTasks()).thenReturn(expected);

        List<TaskEntity> actual = taskService.getAll();

        assertEquals(expected, actual);
        verify(taskRepository).getAllTasks();
    }

    @Test
    void getReturnsTaskWhenItExists() {
        UUID id = UUID.randomUUID();
        TaskEntity expected = new TaskEntity();
        expected.setId(id);

        when(taskRepository.findById(id)).thenReturn(Optional.of(expected));

        TaskEntity actual = taskService.get(id);

        assertSame(expected, actual);
        verify(taskRepository).findById(id);
    }

    @Test
    void getThrowsWhenTaskDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> taskService.get(id));

        assertEquals("Task not found: " + id, exception.getMessage());
        verify(taskRepository).findById(id);
    }
}
