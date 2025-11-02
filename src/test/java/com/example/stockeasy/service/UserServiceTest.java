package com.example.stockeasy.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.stockeasy.domain.User;
import com.example.stockeasy.repo.UserRepository;

class UserServiceTest {

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
    void testRegisterUser_Success() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        User savedUser = new User(username, email, encodedPassword);
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.registerUser(username, email, password, "John", "Doe");

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
    }

    @Test
    void testRegisterUser_UsernameExists() {
        // Given
        String username = "existinguser";
        String email = "test@example.com";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userService.registerUser(username, email, "password", "John", "Doe"));
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_Success() {
        // Given
        String username = "testuser";
        String password = "password123";
        String encodedPassword = "encodedPassword";

        User user = new User(username, "test@example.com", encodedPassword);
        user.setId(1L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.authenticateUser(username, password);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        // Given
        String username = "testuser";
        String password = "wrongpassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userService.authenticateUser(username, password));
        assertEquals("Invalid username or password", exception.getMessage());
    }
}
