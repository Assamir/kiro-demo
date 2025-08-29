package com.insurance.backoffice.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Actuator configuration for health checks and monitoring endpoints.
 * Provides custom health indicators and application information.
 */
@Configuration
public class ActuatorConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.application.name:insurance-backoffice-system}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * Custom health indicator for database connectivity and basic functionality.
     */
    @Bean
    public HealthIndicator databaseHealthIndicator() {
        return () -> {
            try {
                // Test database connectivity
                Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                
                if (result != null && result == 1) {
                    // Test basic table access
                    Long userCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM users", Long.class);
                    
                    Map<String, Object> details = new HashMap<>();
                    details.put("database", "PostgreSQL");
                    details.put("status", "UP");
                    details.put("userCount", userCount);
                    details.put("lastChecked", LocalDateTime.now());
                    
                    return Health.up()
                        .withDetails(details)
                        .build();
                } else {
                    return Health.down()
                        .withDetail("error", "Database query returned unexpected result")
                        .build();
                }
            } catch (Exception e) {
                return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("lastChecked", LocalDateTime.now())
                    .build();
            }
        };
    }

    /**
     * Custom health indicator for application-specific checks.
     */
    @Bean
    public HealthIndicator applicationHealthIndicator() {
        return () -> {
            try {
                // Check if rating tables are available
                Long ratingTableCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM rating_tables", Long.class);
                
                Map<String, Object> details = new HashMap<>();
                details.put("ratingTablesCount", ratingTableCount);
                details.put("profile", activeProfile);
                details.put("lastChecked", LocalDateTime.now());
                
                if (ratingTableCount != null && ratingTableCount > 0) {
                    return Health.up()
                        .withDetails(details)
                        .build();
                } else {
                    return Health.down()
                        .withDetail("error", "No rating tables found")
                        .withDetails(details)
                        .build();
                }
            } catch (Exception e) {
                return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("lastChecked", LocalDateTime.now())
                    .build();
            }
        };
    }

    /**
     * Custom info contributor for application metadata.
     */
    @Bean
    public InfoContributor applicationInfoContributor() {
        return builder -> {
            Map<String, Object> appInfo = new HashMap<>();
            appInfo.put("name", applicationName);
            appInfo.put("profile", activeProfile);
            appInfo.put("startTime", LocalDateTime.now());
            appInfo.put("javaVersion", System.getProperty("java.version"));
            appInfo.put("javaVendor", System.getProperty("java.vendor"));
            appInfo.put("osName", System.getProperty("os.name"));
            appInfo.put("osVersion", System.getProperty("os.version"));
            
            // Runtime information
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> runtimeInfo = new HashMap<>();
            runtimeInfo.put("maxMemory", runtime.maxMemory());
            runtimeInfo.put("totalMemory", runtime.totalMemory());
            runtimeInfo.put("freeMemory", runtime.freeMemory());
            runtimeInfo.put("availableProcessors", runtime.availableProcessors());
            
            builder.withDetail("application", appInfo);
            builder.withDetail("runtime", runtimeInfo);
        };
    }
}