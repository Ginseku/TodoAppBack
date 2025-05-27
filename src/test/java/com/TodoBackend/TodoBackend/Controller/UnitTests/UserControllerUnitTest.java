package com.TodoBackend.TodoBackend.Controller.UnitTests;

import com.TodoBackend.TodoBackend.Controller.UserController;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setUserId(1L);
        user.setEmail("user@example.com");
        user.setUsername("testuser");
    }

    @Test
    void testCreateUser() {
        when(userService.createUser(any(AppUser.class))).thenReturn(user);

        ResponseEntity<AppUser> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService).createUser(user);
    }

    @Test
    void testGetAllUsers() {
        AppUser user2 = new AppUser();
        user2.setUserId(2L);
        user2.setEmail("user2@example.com");
        user2.setUsername("testuser2");

        List<AppUser> users = Arrays.asList(user, user2);
        when(userService.getAllUsers()).thenReturn(users);

        List<AppUser> response = userController.getAllUsers();

        assertEquals(2, response.size());
        assertEquals("user@example.com", response.get(0).getEmail());
        assertEquals("user2@example.com", response.get(1).getEmail());
        verify(userService).getAllUsers();
    }

    @Test
    void testGetUserById_Found() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<AppUser> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<AppUser> response = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).deleteUser(1L);
    }
}
