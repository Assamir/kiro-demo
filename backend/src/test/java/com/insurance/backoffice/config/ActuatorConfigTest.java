package com.insurance.backoffice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ActuatorConfig.
 * Tests custom health indicators and info contributors.
 */
@SpringBootTest(classes = ActuatorConfig.class)
@ActiveProfiles("test")
class ActuatorConfigTest {

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateDatabaseHealthIndicator() {
        // Given
        ActuatorConfig config = new ActuatorConfig();
        config.jdbcTemplate = jdbcTemplate;
        config.applicationName = "test-app";
        config.activeProfile = "test";

        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class)).thenReturn(5L);

        // When
        HealthIndicator healthIndicator = config.databaseHealthIndicator();
        Health health = healthIndicator.health();

        // Then
        assertThat(health.getStatus().getCode()).isEqualTo("UP");
        assertThat(health.getDetails()).containsKey("database");
        assertThat(health.getDetails()).containsKey("userCount");
        assertThat(health.getDetails().get("userCount")).isEqualTo(5L);
    }

    @Test
    void shouldCreateApplicationHealthIndicator() {
        // Given
        ActuatorConfig config = new ActuatorConfig();
        config.jdbcTemplate = jdbcTemplate;
        config.applicationName = "test-app";
        config.activeProfile = "test";

        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM rating_tables", Long.class)).thenReturn(10L);

        // When
        HealthIndicator healthIndicator = config.applicationHealthIndicator();
        Health health = healthIndicator.health();

        // Then
        assertThat(health.getStatus().getCode()).isEqualTo("UP");
        assertThat(health.getDetails()).containsKey("ratingTablesCount");
        assertThat(health.getDetails()).containsKey("profile");
        assertThat(health.getDetails().get("ratingTablesCount")).isEqualTo(10L);
        assertThat(health.getDetails().get("profile")).isEqualTo("test");
    }

    @Test
    void shouldCreateApplicationInfoContributor() {
        // Given
        ActuatorConfig config = new ActuatorConfig();
        config.applicationName = "test-app";
        config.activeProfile = "test";

        // When
        InfoContributor infoContributor = config.applicationInfoContributor();
        Info.Builder builder = new Info.Builder();
        infoContributor.contribute(builder);
        Info info = builder.build();

        // Then
        assertThat(info.getDetails()).containsKey("application");
        assertThat(info.getDetails()).containsKey("runtime");
        
        @SuppressWarnings("unchecked")
        var appInfo = (java.util.Map<String, Object>) info.getDetails().get("application");
        assertThat(appInfo.get("name")).isEqualTo("test-app");
        assertThat(appInfo.get("profile")).isEqualTo("test");
    }
}