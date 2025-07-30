package com.insurance.backoffice.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.backoffice.application.service.AuthenticationService;
import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.interfaces.dto.LoginRequest;
import com.insurance.backoffice.interfaces.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController class.
 * Clean Code: Web layer testing with mocked service dependencies.
 */
@WebMvcTest(controllers = AuthController.class, 
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthenticationService authenticationService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        LoginResponse loginResponse = LoginResponse.of(
                "jwt.token.here",
                "test@example.com",
                "Test User",
                UserRole.OPERATOR,
                86400000L
        );
        
        when(authenticationService.authenticate(any(LoginRequest.class)))
                .thenReturn(loginResponse);
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.role").value("OPERATOR"))
                .andExpect(jsonPath("$.expiresIn").value(86400000L));
        
        verify(authenticationService).authenticate(any(LoginRequest.class));
    }
    
    @Test
    void shouldReturnUnauthorizedWhenLoginFails() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");
        
        when(authenticationService.authenticate(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
        
        verify(authenticationService).authenticate(any(LoginRequest.class));
    }
    
    @Test
    void shouldReturnBadRequestWhenLoginDataIsInvalid() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("", ""); // Invalid email and password
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verifyNoInteractions(authenticationService);
    }
    
    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("invalid-email", "password");
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verifyNoInteractions(authenticationService);
    }
    
    @Test
    void shouldGetCurrentUserSuccessfully() throws Exception {
        // Given
        User testUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .role(UserRole.OPERATOR)
                .build();
        
        when(authenticationService.validateToken("jwt.token.here"))
                .thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer jwt.token.here"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.role").value("OPERATOR"));
        
        verify(authenticationService).validateToken("jwt.token.here");
    }
    
    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
        // Given
        when(authenticationService.validateToken("invalid.token"))
                .thenThrow(new BadCredentialsException("Invalid token"));
        
        // When & Then
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized());
        
        verify(authenticationService).validateToken("invalid.token");
    }
    
    @Test
    void shouldReturnUnauthorizedWhenAuthorizationHeaderIsMissing() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
        
        verifyNoInteractions(authenticationService);
    }
    
    @Test
    void shouldReturnUnauthorizedWhenAuthorizationHeaderIsInvalid() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Basic sometoken"))
                .andExpect(status().isUnauthorized());
        
        verifyNoInteractions(authenticationService);
    }
    
    @Test
    @WithMockUser
    void shouldLogoutSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf()))
                .andExpect(status().isOk());
        
        verifyNoInteractions(authenticationService);
    }
    
    @Test
    void shouldGetCurrentUserForAdminRole() throws Exception {
        // Given
        User adminUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password("password")
                .role(UserRole.ADMIN)
                .build();
        
        when(authenticationService.validateToken("admin.jwt.token"))
                .thenReturn(adminUser);
        
        // When & Then
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer admin.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.fullName").value("Admin User"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
        
        verify(authenticationService).validateToken("admin.jwt.token");
    }
}