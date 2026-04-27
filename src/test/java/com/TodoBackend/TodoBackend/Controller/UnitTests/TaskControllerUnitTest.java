package com.TodoBackend.TodoBackend.Controller.UnitTests;

import com.TodoBackend.TodoBackend.Controller.TaskController;
import com.TodoBackend.TodoBackend.Model.AppTask;
import com.TodoBackend.TodoBackend.Service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class TaskControllerUnitTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        mockPrincipal = () -> "test@example.com"; // мок Principal
    }

    @Test
    void testCreateTask() {
        AppTask inputTask = new AppTask();
        inputTask.setTitle("Test Task");

        AppTask savedTask = new AppTask();
        savedTask.setNoteId(1L);
        savedTask.setTitle("Test Task");

        when(taskService.createTask(any(AppTask.class), eq("test@example.com")))
                .thenReturn(savedTask);
        ResponseEntity<AppTask> response = taskController.createTask(inputTask, mockPrincipal);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(savedTask, response.getBody());
        verify(taskService).createTask(inputTask, "test@example.com");
    }

    @Test
    void testGetAllTask() {
        AppTask task1 = new AppTask();
        task1.setTitle("Task 1");
        AppTask task2 = new AppTask();
        task2.setTitle("Task 2");

        List<AppTask> mockTasks = Arrays.asList(task1, task2);

        when(taskService.getTasksByEmail("test@example.com")).thenReturn(mockTasks);

        List<AppTask> response = taskController.getAllTask(mockPrincipal);

        assertEquals(2, response.size());
        assertEquals("Task 1", response.get(0).getTitle());
        verify(taskService).getTasksByEmail("test@example.com");
    }

    @Test
    void testGetTaskById_Found() {
        AppTask task = new AppTask();
        task.setNoteId(1L);
        task.setTitle("Task");

        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        ResponseEntity<AppTask> response = taskController.getTaskById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(task, response.getBody());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

        ResponseEntity<AppTask> response = taskController.getTaskById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteTask() {
        doNothing().when(taskService).deleteTaskByEmail(1L, "test@example.com");

        ResponseEntity<Void> response = taskController.deleteTask(1L, mockPrincipal);

        assertEquals(200, response.getStatusCodeValue());
        verify(taskService).deleteTaskByEmail(1L, "test@example.com");
    }
}