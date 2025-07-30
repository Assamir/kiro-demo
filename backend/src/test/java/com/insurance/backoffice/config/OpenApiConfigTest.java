package com.insurance.backoffice.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for OpenAPI configuration.
 * Verifies that Swagger/OpenAPI documentation is properly configured.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenApiConfigTest {
    
    @Autowired
    private OpenAPI openAPI;
    
    @Test
    void shouldConfigureOpenAPI() {
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Insurance Backoffice System API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
    }
    
    @Test
    void shouldHaveSecuritySchemes() {
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("Bearer Authentication");
    }
    
    @Test
    void shouldHaveServers() {
        assertThat(openAPI.getServers()).isNotNull();
        assertThat(openAPI.getServers()).hasSize(2);
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://localhost:8080");
        assertThat(openAPI.getServers().get(1).getUrl()).isEqualTo("https://api.insurance-backoffice.com");
    }
}