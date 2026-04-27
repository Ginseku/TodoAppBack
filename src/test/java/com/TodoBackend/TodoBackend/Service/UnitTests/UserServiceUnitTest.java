package com.TodoBackend.TodoBackend.Service.UnitTests;

import com.TodoBackend.TodoBackend.DTO.RegistrationRequest;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import com.TodoBackend.TodoBackend.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private AppUser user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new AppUser();
        user.setEmail("test@example.com");
        user.setPassword("plainpassword");
    }

    @Test
    void createUser_success() {
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser created = userService.createUser(user);

        assertEquals("encodedPassword", created.getPassword());
        assertNotNull(created.getCreateData());
        verify(userRepository, times(1)).save(created);
    }

    @Test
    void registerUser_success() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("newuser@example.com");
        request.setUsername("newuser");
        request.setPassword("newpassword");

        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = userService.registerUser(request);

        assertNotNull(token);
        assertEquals(6, token.length());

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();
        assertEquals("newuser@example.com", savedUser.getEmail());
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertFalse(savedUser.isEnabled());
        assertNotNull(savedUser.getEmailConfirmToken());
        assertNotNull(savedUser.getTokenExpiry());
    }

    @Test
    void registerUser_emailExists_throwsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("existing@example.com");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new AppUser()));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.registerUser(request));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void confirmEmail_success() {
        AppUser userWithToken = new AppUser();
        userWithToken.setEmailConfirmToken("token123");
        userWithToken.setTokenExpiry(LocalDateTime.now().plusHours(1));
        userWithToken.setEnabled(false);

        when(userRepository.findByEmailConfirmToken("token123")).thenReturn(Optional.of(userWithToken));
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.confirmEmail("token123");

        assertTrue(userWithToken.isEnabled());
        assertNull(userWithToken.getEmailConfirmToken());
        verify(userRepository).save(userWithToken);
    }

    @Test
    void confirmEmail_invalidToken_throwsException() {
        when(userRepository.findByEmailConfirmToken("wrongtoken")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.confirmEmail("wrongtoken"));

        assertEquals("Неверный токен", exception.getMessage());
    }

    @Test
    void confirmEmail_tokenExpired_throwsException() {
        AppUser expiredUser = new AppUser();
        expiredUser.setEmailConfirmToken("tokenExpired");
        expiredUser.setTokenExpiry(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmailConfirmToken("tokenExpired")).thenReturn(Optional.of(expiredUser));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.confirmEmail("tokenExpired"));

        assertEquals("Токен истек", exception.getMessage());
    }

    @Test
    void getUserById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<AppUser> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<AppUser> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllUsers_success() {
        List<AppUser> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<AppUser> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
    }

    @Test
    void deleteUser_success() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
