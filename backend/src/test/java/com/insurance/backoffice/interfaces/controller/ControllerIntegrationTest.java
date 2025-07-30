package com.insurance.backoffice.interfaces.controller;

import com.insurance.backoffice.application.service.AuthenticationService;
import com.insurance.backoffice.application.service.PolicyService;
import com.insurance.backoffice.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify that controllers are properly configured and can be instantiated.
 * This test verifies that the REST API controllers are working with Swagger documentation.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ControllerIntegrationTest {
    
    @Autowired
    private UserController userController;
    
    @Autowired
    private PolicyController policyController;
    
    @Autowired
    private AuthController authController;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private PolicyService policyService;
    
    @MockBean
    private AuthenticationService authenticationService;
    
    @Test
    void shouldLoadUserController() {
        assertThat(userController).isNotNull();
    }
    
    @Test
    void shouldLoadPolicyController() {
        assertThat(policyController).isNotNull();
    }
    
    @Test
    void shouldLoadAuthController() {
        assertThat(authController).isNotNull();
    }
    
    @Test
    void shouldHaveSwaggerDocumentationAnnotations() {
        // Verify that controllers have proper Swagger annotations
        assertThat(UserController.class.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)).isTrue();
        assertThat(PolicyController.class.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)).isTrue();
        assertThat(AuthController.class.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)).isTrue();
    }
    
    @Test
    void shouldHaveSecurityRequirements() {
        // Verify that controllers have proper security annotations
        assertThat(UserController.class.isAnnotationPresent(io.swagger.v3.oas.annotations.security.SecurityRequirement.class)).isTrue();
        assertThat(PolicyController.class.isAnnotationPresent(io.swagger.v3.oas.annotations.security.SecurityRequirement.class)).isTrue();
    }
}