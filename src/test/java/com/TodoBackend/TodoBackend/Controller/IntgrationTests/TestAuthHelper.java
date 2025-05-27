package com.TodoBackend.TodoBackend.Controller.IntgrationTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestAuthHelper {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String loginAndGetToken(String email, String password) throws Exception {
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
