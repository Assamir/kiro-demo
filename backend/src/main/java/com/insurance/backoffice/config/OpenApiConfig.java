package com.insurance.backoffice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the Insurance Backoffice System.
 * Clean Code: Centralized API documentation configuration with comprehensive metadata.
 */
@Configuration
public class OpenApiConfig {
    
    /**
     * Configures OpenAPI documentation with security schemes and metadata.
     * Clean Code: Single responsibility for API documentation setup.
     * 
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServers())
                .addSecurityItem(createSecurityRequirement())
                .components(createComponents());
    }
    
    /**
     * Creates API information metadata.
     * Clean Code: Extracted method for API info configuration.
     * 
     * @return API info object
     */
    private Info createApiInfo() {
        return new Info()
                .title("Insurance Backoffice System API")
                .description("REST API for managing car insurance policies, users, and administrative functions. " +
                           "Supports three types of insurance (OC, AC, NNW) with role-based access control.")
                .version("1.0.0")
                .contact(createContact())
                .license(createLicense());
    }
    
    /**
     * Creates contact information for the API.
     * Clean Code: Extracted method for contact details.
     * 
     * @return contact object
     */
    private Contact createContact() {
        return new Contact()
                .name("Insurance Backoffice Team")
                .email("support@insurance-backoffice.com")
                .url("https://insurance-backoffice.com");
    }
    
    /**
     * Creates license information for the API.
     * Clean Code: Extracted method for license details.
     * 
     * @return license object
     */
    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }
    
    /**
     * Creates server configurations for different environments.
     * Clean Code: Extracted method for server configuration.
     * 
     * @return list of server configurations
     */
    private List<Server> createServers() {
        Server developmentServer = new Server()
                .url("http://localhost:8080")
                .description("Development server");
        
        Server productionServer = new Server()
                .url("https://api.insurance-backoffice.com")
                .description("Production server");
        
        return List.of(developmentServer, productionServer);
    }
    
    /**
     * Creates security requirement for JWT authentication.
     * Clean Code: Extracted method for security configuration.
     * 
     * @return security requirement object
     */
    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList("Bearer Authentication");
    }
    
    /**
     * Creates components including security schemes.
     * Clean Code: Extracted method for components configuration.
     * 
     * @return components object with security schemes
     */
    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", createSecurityScheme());
    }
    
    /**
     * Creates JWT security scheme configuration.
     * Clean Code: Extracted method for JWT security scheme.
     * 
     * @return security scheme object
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT token for authentication. Format: Bearer {token}");
    }
}