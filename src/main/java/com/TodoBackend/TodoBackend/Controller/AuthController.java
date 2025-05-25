package com.TodoBackend.TodoBackend.Controller;

import com.TodoBackend.TodoBackend.DTO.RegistrationRequest;
import com.TodoBackend.TodoBackend.Service.EmailService;
import com.TodoBackend.TodoBackend.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final EmailService emailService;

    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    // Регистрация
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        System.out.println("Пришел запрос на регистрацию: " + request.getEmail());
        String token = userService.registerUser(request);
        emailService.sendConfirmationEmail(request.getEmail(), token);
        return ResponseEntity.ok("Confirmation code sent to email");
    }

    // Подтверждение email
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        userService.confirmEmail(token);
        return ResponseEntity.ok("Email confirmed");
    }
}
