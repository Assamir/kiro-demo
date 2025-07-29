package com.insurance.backoffice.config;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SecurityConfig class.
 * Clean Code: Integration testing to verify role-based access control rules.
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityConfigIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private User adminUser;
    private User operatorUser;
    
    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.ADMIN)
                .build();
        
        operatorUser = User.builder()
                .firstName("Operator")
                .lastName("User")
                .email("operator@example.com")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.OPERATOR)
                .build();
        
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(adminUser));
        when(userRepository.findByEmail("operator@example.com"))
                .thenReturn(Optional.of(operatorUser));
    }
    
    @Test
    void shouldAllowPublicAccessToAuthEndpoints() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized()); // Unauthorized due to invalid credentials, not access denied
    }
    
    @Test
    void shouldAllowPublicAccessToSwaggerEndpoints() throws Exception {
        // When & Then
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
    
    @Test
    void shouldDenyUnauthenticatedAccessToProtectedEndpoints() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccessToUserManagement() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
        
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, not access denied
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldDenyOperatorAccessToUserManagement() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
        
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccessToPolicyManagement() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk());
        
        mockMvc.perform(post("/api/policies")
                .with(csrf())
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, not access denied
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldAllowOperatorAccessToPolicyManagement() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk());
        
        mockMvc.perform(post("/api/policies")
                .with(csrf())
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, not access denied
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccessToRatingEndpoints() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/rating/tables/OC"))
                .andExpect(status().isNotFound()); // Not found due to missing implementation, not access denied
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldAllowOperatorAccessToRatingEndpoints() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/rating/tables/OC"))
                .andExpect(status().isNotFound()); // Not found due to missing implementation, not access denied
    }
    
    @Test
    void shouldDenyUnauthenticatedAccessToRatingEndpoints() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/rating/tables/OC"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccessToSpecificUserEndpoints() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound()); // Not found due to missing implementation, not access denied
        
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isNotFound()); // Not found due to missing implementation, not access denied
        
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isNotFound()); // Not found due to missing implementation, not access denied
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldDenyOperatorAccessToSpecificUserEndpoints() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
        
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
        
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccessToPolicyPdfGeneration() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/policies/1/pdf")
                .with(csrf()))
                .andExpect(status().isNotFound()); // Not found due to missing implementation, not access denied
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldAllowOperatorAccessToPolicyPdfGeneration() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/policies/1/pdf")
                .with(csrf()))
                .andExpect(status().isNotFound()); // Not found due to missing implementation, not access denied
    }
    
    @Test
    void shouldDenyUnauthenticatedAccessToPolicyPdfGeneration() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/policies/1/pdf")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}