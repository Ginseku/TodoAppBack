package com.TodoBackend.TodoBackend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


public class RegistrationRequest {
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Incorrect email")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 6, message = "The password must contain at least 6 characters")
    private String password;

    private String username;


    public RegistrationRequest() {}

    public RegistrationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
