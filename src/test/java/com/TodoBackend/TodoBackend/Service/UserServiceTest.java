package com.TodoBackend.TodoBackend.Service;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createUser_shouldEncodePasswordAndSaveUser() {
        AppUser user = new AppUser();
        user.setUsername("testUser");
        user.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(AppUser.class))).thenAnswer(i -> i.getArguments()[0]);

        AppUser result = userService.createUser(user);


        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getCreateData()).isNotNull();
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode("plainPassword");
    }


    @Test
    void getUserById_shouldReturnUser() {

        AppUser user = new AppUser(1L, "user1", "password", "email", LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));


        Optional<AppUser> result = userService.getUserById(1L);


        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user1");
        verify(userRepository, times(1)).findById(1L);
    }


    @Test
    void getAllUsers_shouldReturnUserList() {

        List<AppUser> users = List.of(
                new AppUser(1L, "user1", "pass", "email1", LocalDateTime.now()),
                new AppUser(2L, "user2", "pass", "email2", LocalDateTime.now())
        );
        when(userRepository.findAll()).thenReturn(users);


        List<AppUser> result = userService.getAllUsers();


        assertThat(result).hasSize(2);
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void deleteUser_shouldCallDeleteById() {

        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}

