package com.insurance.backoffice.integration;

import com.insurance.backoffice.interfaces.dto.CreateUserRequest;
import com.insurance.backoffice.interfaces.dto.UpdateUserRequest;
import com.insurance.backoffice.interfaces.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for user management endpoints.
 * Tests complete CRUD operations for user management with proper authorization.
 */
class UserManagementIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldAllowAdminToCreateUser() {
        // Given
        CreateUserRequest createRequest = new CreateUserRequest(
                "New", "User", "newuser@test.com", "password123", "OPERATOR"
        );
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createRequest, createAdminHeaders());

        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                getBaseUrl() + "/users",
                HttpMethod.POST,
                request,
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("newuser@test.com");
        assertThat(response.getBody().getFirstName()).isEqualTo("New");
        assertThat(response.getBody().getLastName()).isEqualTo("User");
        assertThat(response.getBody().getRole()).isEqualTo("OPERATOR");
    }

    @Test
    void shouldPreventOperatorFromCreatingUser() {
        // Given
        CreateUserRequest createRequest = new CreateUserRequest(
                "New", "User", "newuser@test.com", "password123", "OPERATOR"
        );
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createRequest, createOperatorHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/users",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldAllowAdminToListAllUsers() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createAdminHeaders());

        // When
        ResponseEntity<List<UserResponse>> response = restTemplate.exchange(
                getBaseUrl() + "/users",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<UserResponse>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2); // Admin and Operator created in setup
        
        // Verify both test users are present
        List<String> emails = response.getBody().stream()
                .map(UserResponse::getEmail)
                .toList();
        assertThat(emails).contains("admin@test.com", "operator@test.com");
    }

    @Test
    void shouldPreventOperatorFromListingUsers() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/users",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldAllowAdminToGetUserById() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createAdminHeaders());

        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                getBaseUrl() + "/users/" + testOperator.getId(),
                HttpMethod.GET,
                request,
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("operator@test.com");
        assertThat(response.getBody().getRole()).isEqualTo("OPERATOR");
    }

    @Test
    void shouldAllowAdminToUpdateUser() {
        // Given
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "Updated", "Name", "updated@test.com", "ADMIN"
        );
        HttpEntity<UpdateUserRequest> request = new HttpEntity<>(updateRequest, createAdminHeaders());

        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                getBaseUrl() + "/users/" + testOperator.getId(),
                HttpMethod.PUT,
                request,
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFirstName()).isEqualTo("Updated");
        assertThat(response.getBody().getLastName()).isEqualTo("Name");
        assertThat(response.getBody().getEmail()).isEqualTo("updated@test.com");
        assertThat(response.getBody().getRole()).isEqualTo("ADMIN");
    }

    @Test
    void shouldAllowAdminToDeleteUser() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createAdminHeaders());

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/users/" + testOperator.getId(),
                HttpMethod.DELETE,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Verify user is deleted
        ResponseEntity<String> getResponse = restTemplate.exchange(
                getBaseUrl() + "/users/" + testOperator.getId(),
                HttpMethod.GET,
                request,
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRejectDuplicateEmailWhenCreatingUser() {
        // Given - try to create user with existing email
        CreateUserRequest createRequest = new CreateUserRequest(
                "Duplicate", "User", "admin@test.com", "password123", "OPERATOR"
        );
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createRequest, createAdminHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/users",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldValidateRequiredFieldsWhenCreatingUser() {
        // Given - request with missing required fields
        CreateUserRequest createRequest = new CreateUserRequest(
                "", "", "", "", ""
        );
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createRequest, createAdminHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/users",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn404WhenUserNotFound() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createAdminHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/users/99999",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}