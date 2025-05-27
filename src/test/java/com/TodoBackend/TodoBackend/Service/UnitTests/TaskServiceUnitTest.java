package com.TodoBackend.TodoBackend.Service.UnitTests;
import com.TodoBackend.TodoBackend.Model.AppTask;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.TaskRepository;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import com.TodoBackend.TodoBackend.Service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private AppUser user;
    private AppTask task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new AppUser();
        user.setEmail("test@example.com");

        task = new AppTask();
        task.setTitle("Test Task");
        task.setContent("Content");
    }

    @Test
    void createTask_success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(AppTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppTask created = taskService.createTask(task, user.getEmail());

        assertNotNull(created.getTaskCreatedTime());
        assertEquals(user, created.getUser());
        assertEquals("Test Task", created.getTitle());

        verify(taskRepository, times(1)).save(created);
    }

    @Test
    void createTask_userNotFound_throwsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> taskService.createTask(task, user.getEmail()));

        assertEquals("User not found", exception.getMessage());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void getTaskById_found() {
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        Optional<AppTask> result = taskService.getTaskById(id);

        assertTrue(result.isPresent());
        assertEquals(task, result.get());
    }

    @Test
    void getTaskById_notFound() {
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        Optional<AppTask> result = taskService.getTaskById(id);

        assertFalse(result.isPresent());
    }

    @Test
    void getTasksByEmail_success() {
        List<AppTask> tasks = List.of(task);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.findByUser(user)).thenReturn(tasks);

        List<AppTask> result = taskService.getTasksByEmail(user.getEmail());

        assertEquals(1, result.size());
        assertEquals(task, result.get(0));
    }

    @Test
    void getTasksByEmail_userNotFound_throwsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> taskService.getTasksByEmail(user.getEmail()));

        assertEquals("User not found", exception.getMessage());
        verify(taskRepository, never()).findByUser(any());
    }

    @Test
    void deleteTaskByEmail_success() {
        Long taskId = 1L;
        task.setUser(user);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        taskService.deleteTaskByEmail(taskId, user.getEmail());

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void deleteTaskByEmail_taskNotFound_throwsException() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.deleteTaskByEmail(taskId, user.getEmail()));

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void deleteTaskByEmail_userNotFound_throwsException() {
        Long taskId = 1L;

        task.setUser(user);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> taskService.deleteTaskByEmail(taskId, user.getEmail()));

        assertEquals("User not found", exception.getMessage());
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void deleteTaskByEmail_notOwner_throwsException() {
        Long taskId = 1L;
        AppUser otherUser = new AppUser();
        otherUser.setEmail("other@example.com");

        task.setUser(otherUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        SecurityException exception = assertThrows(SecurityException.class,
                () -> taskService.deleteTaskByEmail(taskId, user.getEmail()));

        assertEquals("You don't have permission to delete this task", exception.getMessage());
        verify(taskRepository, never()).deleteById(any());
    }
}
