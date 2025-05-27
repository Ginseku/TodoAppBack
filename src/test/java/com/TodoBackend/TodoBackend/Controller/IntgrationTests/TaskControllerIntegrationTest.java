package com.TodoBackend.TodoBackend.Controller.IntgrationTests;

import com.TodoBackend.TodoBackend.Model.AppTask;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.TaskRepository;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String email = "testuser@example.com";
    private String password = "password";

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Test
    void testCreateAndGetAndDeleteTask() throws Exception {
        // 1. Авторизация (получаем JWT-токен)
        String token = TestAuthHelper.loginAndGetToken(mockMvc, email, password);

        // 2. Создание задачи
        AppTask task = new AppTask();
        task.setTitle("Интеграционный тест");
        task.setContent("Проверка создания");

        String response = mockMvc.perform(post("/task/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Интеграционный тест"))
                .andReturn().getResponse().getContentAsString();

        AppTask createdTask = objectMapper.readValue(response, AppTask.class);

        // 3. Получение всех задач
        mockMvc.perform(get("/task/getAll")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Интеграционный тест"));

        // 4. Удаление задачи
        mockMvc.perform(delete("/task/deleteById/" + createdTask.getNoteId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 5. Проверка, что задач нет
        mockMvc.perform(get("/task/getAll")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Вложенный класс-хелпер для авторизации и получения JWT
    public static class TestAuthHelper {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static String loginAndGetToken(MockMvc mockMvc, String email, String password) throws Exception {
            String loginJson = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

            MvcResult result = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andReturn();

            int status = result.getResponse().getStatus();
            if (status != 200) {
                throw new RuntimeException("Login failed with status: " + status);
            }

            String responseJson = result.getResponse().getContentAsString();
            JsonNode node = objectMapper.readTree(responseJson);
            JsonNode tokenNode = node.get("token");
            if (tokenNode == null) {
                throw new RuntimeException("Token not found in login response");
            }
            return tokenNode.asText();
        }
    }
}
