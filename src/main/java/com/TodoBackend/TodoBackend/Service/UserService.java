package com.TodoBackend.TodoBackend.Service;


import com.TodoBackend.TodoBackend.DTO.RegistrationRequest;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    public AppUser createUser(AppUser user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateData(LocalDateTime.now());
        return userRepository.save(user);
    }
    public String registerUser(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email уже занят");
        }

        AppUser user = new AppUser();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String token = generateToken();
        user.setEmailConfirmToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);
        return token;
    }

    public void confirmEmail(String token) {
        AppUser user = userRepository.findByEmailConfirmToken(token)
                .orElseThrow(() -> new RuntimeException("Неверный токен"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Токен истек");
        }

        user.setEnabled(true);
        user.setEmailConfirmToken(null);
        userRepository.save(user);
    }
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

    public Optional<AppUser> getUserById(Long id){
        return userRepository.findById(id);
    }

    public List<AppUser> getAllUsers(){
        return userRepository.findAll();
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
