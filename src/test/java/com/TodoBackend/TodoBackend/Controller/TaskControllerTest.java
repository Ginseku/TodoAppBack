package com.TodoBackend.TodoBackend.Controller;

import com.TodoBackend.TodoBackend.Model.AppTask;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.TaskRepository;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import com.TodoBackend.TodoBackend.Service.TaskService;
import com.TodoBackend.TodoBackend.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @TestConfiguration
    static class TestConfig{
        @Bean
        public UserService userService(){
            return Mockito.mock(UserService.class);
        }
       @Bean
       public TaskService taskService(){
           return Mockito.mock(TaskService.class);
       }
       @Bean
       public TaskRepository taskRepository(){
           return Mockito.mock(TaskRepository.class);
       }
       @Bean
       public UserRepository userRepository(){
           return Mockito.mock(UserRepository.class);
       }
   }
   @Autowired
   private UserService userService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
   @Autowired
    private TaskService taskService;

    @Test
    @WithMockUser(username = "Admin", roles = {"USER"})
    void getAllTasksTest() throws Exception{
        List<AppTask> appTasksList = List.of(new AppTask(1L, "Test Task 1", "", "Hi there", LocalDateTime.now(),null));

        Mockito.when(taskService.getAllTasks()).thenReturn(appTasksList);

        mockMvc.perform(get("/task/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task 1"))
                .andExpect(jsonPath("$[0].content").value("Hi there"));
}

    @Test
    @WithMockUser(username = "Admin", roles = {"USER"})
    void getTaskByIdTest() throws Exception {
        AppTask task = new AppTask(1L, "Test Task 1", "", "Hi there", LocalDateTime.now(), null);

        Mockito.when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/task/getById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noteId").value(1))
                .andExpect(jsonPath("$.title").value("Test Task 1"));
    }

    @Test
    @WithMockUser(username = "Admin", roles = {"USER"})
    void createTaskTest() throws Exception {
        AppTask task = new AppTask();
        task.setTitle("New Task");
        task.setContent("Task Content");

        AppUser mockUser = new AppUser();
        mockUser.setUsername("Admin");

        AppTask savedTask = new AppTask();
        savedTask.setNoteId(1L);
        savedTask.setTitle("New Task");
        savedTask.setContent("Task Content");
        savedTask.setTaskCreatedTime(LocalDateTime.now());
        savedTask.setUser(mockUser);

        when(userRepository.findByUsername("Admin")).thenReturn(Optional.of(mockUser));
        when(taskService.createTask(Mockito.any(AppTask.class), Mockito.eq("Admin"))).thenReturn(savedTask);

        String requestBody = """
        {
            "title": "New Task",
            "content": "Task Content"
        }
    """;

        mockMvc.perform(post("/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.content").value("Task Content"));
    }



    @Test
    @WithMockUser(username = "Admin", roles = {"ADMIN"})
    void deleteTaskById() throws Exception{
        Long taskId = 1L;

        Mockito.doNothing().when(taskService).deleteTask(taskId);

        mockMvc.perform(delete("/task/deleteById/{id}", taskId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }




}
