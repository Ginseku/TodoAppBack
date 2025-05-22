package com.TodoBackend.TodoBackend.Service;


import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
