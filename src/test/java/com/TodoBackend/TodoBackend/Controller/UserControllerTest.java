package com.TodoBackend.TodoBackend.Controller;

import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.TaskRepository;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import com.TodoBackend.TodoBackend.Service.TaskService;
import com.TodoBackend.TodoBackend.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
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

import javax.net.ssl.SSLEngineResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
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
        public UserRepository userRepository(){
            return Mockito.mock(UserRepository.class);
        }
        @Bean
        public TaskService taskService(){
            return Mockito.mock(TaskService.class);
        }
        @Bean
        public TaskRepository taskRepository(){
            return Mockito.mock(TaskRepository.class);
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
    @WithMockUser(username = "Admin", roles = {"ADMIN"})
    void createUserTest() throws Exception {
        AppUser testUser = new AppUser();
        testUser.setUserId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("password");

        Mockito.when(userService.createUser(Mockito.any(AppUser.class)))
                .thenReturn(testUser);

        String requestBody = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/createUser")
                        .with(csrf()) // обязательно, иначе 403
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "Admin", roles = {"USER"})
    void getAllUsersTest() throws Exception {
        List<AppUser> appUserList = List.of(new AppUser(1L, "Bady", "password", "email@gmail.com", LocalDateTime.now()));

        Mockito.when(userService.getAllUsers()).thenReturn(appUserList);

        mockMvc.perform(get("/user/getAllUsers")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("Bady"))
                .andExpect(jsonPath("$[0].email").value("email@gmail.com"));

    }

    @Test
    @WithMockUser(username = "Admin", roles = {"USER"})
    void getUserByIdTest() throws Exception {
        AppUser appUserList = new AppUser(1L, "Bady", "password", "email@gmail.com", LocalDateTime.now());

        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(appUserList));

        mockMvc.perform(get("/user/getUserById/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("Bady"))
                .andExpect(jsonPath("$.email").value("email@gmail.com"));

    }

    @Test
    @WithMockUser(username = "Admin", roles = {"ADMIN"})
    void deleteUserByIdTest() throws Exception {
        Long userId = 1L;

        Mockito.doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/user/deleteUserById/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
