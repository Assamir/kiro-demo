package com.insurance.backoffice.interfaces.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for HealthController.
 * Tests custom health check endpoints for monitoring and load balancer integration.
 */
@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthEndpoint healthEndpoint;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldReturnHealthyStatusWhenApplicationIsUp() throws Exception {
        // Given
        Health health = Health.up().build();
        when(healthEndpoint.health()).thenReturn(health);

        // When & Then
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnUnhealthyStatusWhenApplicationIsDown() throws Exception {
        // Given
        Health health = Health.down().build();
        when(healthEndpoint.health()).thenReturn(health);

        // When & Then
        mockMvc.perform(get("/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.message").value("Application is not healthy"));
    }

    @Test
    void shouldReturnLivenessProbeSuccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/health/live"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Application is alive"));
    }

    @Test
    void shouldReturnReadinessProbeSuccessWhenDatabaseIsReady() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

        // When & Then
        mockMvc.perform(get("/health/ready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Application is ready"));
    }

    @Test
    void shouldReturnReadinessProbeFailureWhenDatabaseIsNotReady() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(get("/health/ready"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.error").value("Database not ready: Database connection failed"));
    }

    @Test
    void shouldReturnStartupProbeSuccessWhenTablesExist() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class)).thenReturn(5L);
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM rating_tables", Long.class)).thenReturn(10L);

        // When & Then
        mockMvc.perform(get("/health/startup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Application started successfully"))
                .andExpect(jsonPath("$.userCount").value(5))
                .andExpect(jsonPath("$.ratingTableCount").value(10));
    }

    @Test
    void shouldReturnStartupProbeFailureWhenTablesDoNotExist() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class))
                .thenThrow(new RuntimeException("Table 'users' doesn't exist"));

        // When & Then
        mockMvc.perform(get("/health/startup"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.error").value("Startup check failed: Table 'users' doesn't exist"));
    }
}