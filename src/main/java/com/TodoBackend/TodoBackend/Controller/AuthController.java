package com.TodoBackend.TodoBackend.Controller;

import com.TodoBackend.TodoBackend.Component.JWTToken.JwtTokenProvider;
import com.TodoBackend.TodoBackend.DTO.AuthDTO;
import com.TodoBackend.TodoBackend.DTO.RegistrationRequest;
import com.TodoBackend.TodoBackend.Service.EmailService;
import com.TodoBackend.TodoBackend.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, EmailService emailService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        System.out.println("Пришел запрос на регистрацию: " + request.getEmail());
        String token = userService.registerUser(request);
        emailService.sendConfirmationEmail(request.getEmail(), token);
        return ResponseEntity.ok("Confirmation link sent to email");
    }


    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        userService.confirmEmail(token);
        return ResponseEntity.ok("Email confirmed successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.JwtResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthDTO.JwtResponse(token));
    }
}
