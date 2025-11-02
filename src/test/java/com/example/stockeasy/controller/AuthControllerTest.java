package com.example.stockeasy.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.stockeasy.domain.User;
import com.example.stockeasy.service.AuthService;
import com.example.stockeasy.web.AuthController;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    void testSuccessfulRegistration() throws Exception {
        // Given
        User registeredUser = new User("testuser", "test@example.com", "password");
        registeredUser.setId(1L);
        when(authService.register(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(registeredUser);

        // When & Then
        mockMvc.perform(post("/register")
                .param("username", "testuser")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testFailedRegistration() throws Exception {
        // Given
        when(authService.register(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Registration failed"));

        // When & Then
        mockMvc.perform(post("/register")
                .param("username", "testuser")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        // Given
        User authenticatedUser = new User("testuser", "test@example.com", "password");
        authenticatedUser.setId(1L);
        when(authService.authenticate(anyString(), anyString())).thenReturn(authenticatedUser);

        // When & Then
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void testFailedLogin() throws Exception {
        // Given
        when(authService.authenticate(anyString(), anyString()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }
}
