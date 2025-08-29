package com.insurance.backoffice.interfaces.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom health check controller for load balancer and monitoring integration.
 * Provides simplified health endpoints for external monitoring systems.
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private HealthEndpoint healthEndpoint;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Simple health check endpoint for load balancers.
     * Returns 200 OK if application is healthy, 503 Service Unavailable otherwise.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        try {
            var health = healthEndpoint.health();
            Map<String, Object> response = new HashMap<>();
            
            response.put("status", health.getStatus().getCode());
            response.put("timestamp", LocalDateTime.now());
            
            if (health.getStatus() == Status.UP) {
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Application is not healthy");
                return ResponseEntity.status(503).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(503).body(response);
        }
    }

    /**
     * Liveness probe endpoint for Kubernetes/Docker health checks.
     * Checks if the application is running and can handle requests.
     */
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> liveness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Application is alive");
        return ResponseEntity.ok(response);
    }

    /**
     * Readiness probe endpoint for Kubernetes/Docker health checks.
     * Checks if the application is ready to handle traffic.
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> readiness() {
        try {
            // Test database connectivity
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Application is ready");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "DOWN");
            response.put("error", "Database not ready: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(503).body(response);
        }
    }

    /**
     * Startup probe endpoint for Kubernetes health checks.
     * Checks if the application has started successfully.
     */
    @GetMapping("/startup")
    public ResponseEntity<Map<String, Object>> startup() {
        try {
            // Check if essential tables exist
            Long userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
            Long ratingTableCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM rating_tables", Long.class);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Application started successfully");
            response.put("userCount", userCount);
            response.put("ratingTableCount", ratingTableCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "DOWN");
            response.put("error", "Startup check failed: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(503).body(response);
        }
    }
}