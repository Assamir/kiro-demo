package com.insurance.backoffice.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.backoffice.application.service.EntityNotFoundException;
import com.insurance.backoffice.application.service.UserService;
import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.interfaces.controller.UserController.CreateUserRequest;
import com.insurance.backoffice.interfaces.controller.UserController.UpdateUserRequest;
import com.insurance.backoffice.interfaces.controller.UserController.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController class.
 * Clean Code: Web layer testing with mocked service dependencies.
 */
@WebMvcTest(controllers = UserController.class, 
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
@TestPropertySource(locations = "classpath:application-test.properties")
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsersSuccessfully() throws Exception {
        // Given
        List<UserResponse> users = List.of(
            new UserResponse(1L, "John", "Doe", "john.doe@example.com", UserRole.OPERATOR),
            new UserResponse(2L, "Jane", "Smith", "jane.smith@example.com", UserRole.ADMIN)
        );
        
        List<User> userEntities = createMockUsers();
        when(userService.findAllUsers()).thenReturn(userEntities);
        
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].role").value("OPERATOR"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"))
                .andExpect(jsonPath("$[1].email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));
        
        verify(userService).findAllUsers();
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldReturnForbiddenWhenOperatorTriesToGetAllUsers() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
        
        verifyNoInteractions(userService);
    }
    
    @Test
    void shouldReturnUnauthorizedWhenUnauthenticatedUserTriesToGetAllUsers() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
        
        verifyNoInteractions(userService);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserByIdSuccessfully() throws Exception {
        // Given
        UserResponse user = new UserResponse(1L, "John", "Doe", "john.doe@example.com", UserRole.OPERATOR);
        
        User userEntity = createMockUser(1L, "John", "Doe", "john.doe@example.com", com.insurance.backoffice.domain.UserRole.OPERATOR);
        when(userService.findUserById(1L)).thenReturn(userEntity);
        
        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("OPERATOR"));
        
        verify(userService).findUserById(1L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Given
        when(userService.findUserById(999L)).thenThrow(new EntityNotFoundException("User not found"));
        
        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
        
        verify(userService).findUserById(999L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "John", "Doe", "john.doe@example.com", "password123", UserRole.OPERATOR
        );
        UserResponse createdUser = new UserResponse(1L, "John", "Doe", "john.doe@example.com", UserRole.OPERATOR);
        User createdUserEntity = createMockUser(1L, "John", "Doe", "john.doe@example.com", com.insurance.backoffice.domain.UserRole.OPERATOR);
        
        when(userService.createUser(any(User.class))).thenReturn(createdUserEntity);
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("OPERATOR"));
        
        verify(userService).createUser(any(User.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenCreateUserDataIsInvalid() throws Exception {
        // Given
        CreateUserRequest invalidRequest = new CreateUserRequest(
            "", "", "invalid-email", "", null
        );
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verifyNoInteractions(userService);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        // Given
        CreateUserRequest invalidRequest = new CreateUserRequest(
            "John", "Doe", "invalid-email", "password123", UserRole.OPERATOR
        );
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verifyNoInteractions(userService);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given
        UpdateUserRequest request = new UpdateUserRequest(
            "John", "Smith", "john.smith@example.com", UserRole.ADMIN
        );
        UserResponse updatedUser = new UserResponse(1L, "John", "Smith", "john.smith@example.com", UserRole.ADMIN);
        
        User updatedUserEntity = createMockUser(1L, "John", "Smith", "john.smith@example.com", com.insurance.backoffice.domain.UserRole.ADMIN);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUserEntity);
        
        // When & Then
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("john.smith@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
        
        verify(userService).updateUser(eq(1L), any(User.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        // Given
        UpdateUserRequest request = new UpdateUserRequest(
            "John", "Smith", "john.smith@example.com", UserRole.ADMIN
        );
        
        when(userService.updateUser(eq(999L), any(User.class))).thenThrow(new com.insurance.backoffice.application.service.EntityNotFoundException("User not found"));
        
        // When & Then
        mockMvc.perform(put("/api/users/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(userService).updateUser(eq(999L), any(User.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUpdateUserDataIsInvalid() throws Exception {
        // Given
        UpdateUserRequest invalidRequest = new UpdateUserRequest(
            "", "", "invalid-email", null
        );
        
        // When & Then
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verifyNoInteractions(userService);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserSuccessfully() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
        
        verify(userService).deleteUser(1L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        // Given
        doThrow(new com.insurance.backoffice.application.service.EntityNotFoundException("User not found")).when(userService).deleteUser(999L);
        
        // When & Then
        mockMvc.perform(delete("/api/users/999")
                .with(csrf()))
                .andExpect(status().isNotFound());
        
        verify(userService).deleteUser(999L);
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldReturnForbiddenWhenOperatorTriesToCreateUser() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "John", "Doe", "john.doe@example.com", "password123", UserRole.OPERATOR
        );
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        
        verifyNoInteractions(userService);
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldReturnForbiddenWhenOperatorTriesToUpdateUser() throws Exception {
        // Given
        UpdateUserRequest request = new UpdateUserRequest(
            "John", "Smith", "john.smith@example.com", UserRole.ADMIN
        );
        
        // When & Then
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        
        verifyNoInteractions(userService);
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldReturnForbiddenWhenOperatorTriesToDeleteUser() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
        
        verifyNoInteractions(userService);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUserWithAdminRole() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "Admin", "User", "admin@example.com", "adminpass123", UserRole.ADMIN
        );
        UserResponse createdUser = new UserResponse(1L, "Admin", "User", "admin@example.com", UserRole.ADMIN);
        User createdUserEntity = createMockUser(1L, "Admin", "User", "admin@example.com", com.insurance.backoffice.domain.UserRole.ADMIN);
        
        when(userService.createUser(any(User.class))).thenReturn(createdUserEntity);
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"));
        
        verify(userService).createUser(any(User.class));
    }
    
    // Helper methods for creating mock objects
    private List<User> createMockUsers() {
        return List.of(
            createMockUser(1L, "John", "Doe", "john.doe@example.com", com.insurance.backoffice.domain.UserRole.OPERATOR),
            createMockUser(2L, "Jane", "Smith", "jane.smith@example.com", com.insurance.backoffice.domain.UserRole.ADMIN)
        );
    }
    
    private User createMockUser(Long id, String firstName, String lastName, String email, com.insurance.backoffice.domain.UserRole role) {
        return User.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .password("password")
            .role(role)
            .build();
    }
}