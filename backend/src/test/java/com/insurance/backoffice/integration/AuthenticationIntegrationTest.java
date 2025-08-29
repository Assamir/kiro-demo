package com.insurance.backoffice.integration;

import com.insurance.backoffice.interfaces.dto.LoginRequest;
import com.insurance.backoffice.interfaces.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for authentication endpoints.
 * Tests the complete authentication flow including JWT token generation and validation.
 */
class AuthenticationIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldAuthenticateAdminUserSuccessfully() {
        // Given
        LoginRequest loginRequest = new LoginRequest("admin@test.com", "admin123");
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest);

        // When
        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                getBaseUrl() + "/auth/login",
                HttpMethod.POST,
                request,
                LoginResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();
        assertThat(response.getBody().getUser().getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getBody().getUser().getRole()).isEqualTo("ADMIN");
    }

    @Test
    void shouldAuthenticateOperatorUserSuccessfully() {
        // Given
        LoginRequest loginRequest = new LoginRequest("operator@test.com", "operator123");
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest);

        // When
        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                getBaseUrl() + "/auth/login",
                HttpMethod.POST,
                request,
                LoginResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();
        assertThat(response.getBody().getUser().getEmail()).isEqualTo("operator@test.com");
        assertThat(response.getBody().getUser().getRole()).isEqualTo("OPERATOR");
    }

    @Test
    void shouldRejectInvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest("admin@test.com", "wrongpassword");
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/auth/login",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectNonExistentUser() {
        // Given
        LoginRequest loginRequest = new LoginRequest("nonexistent@test.com", "password");
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/auth/login",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldValidateTokenAndReturnCurrentUser() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createAdminHeaders());

        // When
        ResponseEntity<LoginResponse.UserInfo> response = restTemplate.exchange(
                getBaseUrl() + "/auth/me",
                HttpMethod.GET,
                request,
                LoginResponse.UserInfo.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getBody().getRole()).isEqualTo("ADMIN");
    }

    @Test
    void shouldRejectInvalidToken() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders("invalid-token"));

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/auth/me",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectRequestWithoutToken() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/auth/me",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}