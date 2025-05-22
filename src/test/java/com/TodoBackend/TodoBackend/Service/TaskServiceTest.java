package com.TodoBackend.TodoBackend.Service;

import com.TodoBackend.TodoBackend.Model.AppTask;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.TaskRepository;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask_shouldSetUserAndTimeAndSave() {

        AppTask task = new AppTask();
        AppUser user = new AppUser();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(AppTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppTask result = taskService.createTask(task, "testUser");

        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getTaskCreatedTime()).isNotNull();
        verify(taskRepository, times(1)).save(task);
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void createTask_shouldThrowIfUserNotFound() {
        AppTask task = new AppTask();
        when(userRepository.findByUsername("invalidUser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.createTask(task, "invalidUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findByUsername("invalidUser");
        verify(taskRepository, never()).save(any());
    }

    @Test
    void getTaskById_shouldReturnTask() {
        AppTask task = new AppTask();
        task.setNoteId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<AppTask> result = taskService.getTaskById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNoteId()).isEqualTo(1L);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getAllTasks_shouldReturnTaskList() {
        List<AppTask> tasks = List.of(new AppTask(), new AppTask());

        when(taskRepository.findAll()).thenReturn(tasks);

        List<AppTask> result = taskService.getAllTasks();

        assertThat(result).hasSize(2);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void deleteTask_shouldCallRepositoryDelete() {
        Long taskId = 1L;

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }
}
